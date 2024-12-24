package com.sukui.authr.core.camera

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.NotFoundException
import java.nio.ByteBuffer
import android.util.Base64
import java.net.URLDecoder
import com.google.protobuf.InvalidProtocolBufferException
import com.sukui.authr.core.otp.OtpMigration.Payload
import org.apache.commons.codec.binary.Base32
import java.net.URLEncoder


class QrCodeAnalyzer(
    private inline val onSuccess: (com.google.zxing.Result) -> Unit,
    private inline val onFail: (NotFoundException) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        image.use { imageProxy ->
            val data = imageProxy.planes[0].buffer.toByteArray()
            ZxingDecoder.decodeYuvLuminanceSource(
                data = data,
                dataWidth = imageProxy.width,
                dataHeight = imageProxy.height,
                onSuccess = { result ->
                    Log.d("QR_SCAN", "Decoded Text: ${result.text}")
                    Log.d("QR_SCAN", "Raw Bytes: ${result.rawBytes?.joinToString(", ") ?: "No raw bytes"}")
                    Log.d("QR_SCAN", "Barcode Format: ${result.barcodeFormat}")
                    Log.d("QR_SCAN", "Result Points: ${result.resultPoints?.joinToString { "(${it.x}, ${it.y})" } ?: "No result points"}")

                    if (result.text.startsWith("otpauth://")) {
                        onSuccess(result)
                    } else if (result.text.startsWith("otpauth-migration://")) {
                        val otpLink = decodeOtpAuthMigrationLink(result.text)
                        val modifiedResult = com.google.zxing.Result(
                            otpLink,
                            result.rawBytes,
                            result.resultPoints,
                            result.barcodeFormat
                        )
                        onSuccess(modifiedResult)
                    } else {
                        Log.e("QR_SCAN", "Unsupported QR Code format")
                        onFail
                    }
                },
                onError = onFail
            )
        }
    }


    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val bytes = ByteArray(remaining())
        get(bytes)
        return bytes
    }
}

fun decodeOtpAuthMigrationLink(link: String): String {
    return try {
        val decodedLink = URLDecoder.decode(link, "UTF-8")
        Log.d("DecodedLink", "URL Decoded: $decodedLink")
        val base64Data = decodedLink.split("=", limit = 2).getOrNull(1)
            ?: throw IllegalArgumentException("Invalid OTP migration link: No data parameter found")

        val decodedData = Base64.decode(base64Data, Base64.DEFAULT)
        val otpMigrationPayload = Payload.parseFrom(decodedData)
        val otpParameter = otpMigrationPayload.otpParametersList.firstOrNull()
            ?: throw IllegalArgumentException("No OTP parameters found in the migration link")

        val base32 = Base32()
        val secretBase32 = base32.encodeToString(otpParameter.secret.toByteArray())

        val accountName = URLEncoder.encode(otpParameter.name ?: "Test Token", "UTF-8")
        val issuer = URLEncoder.encode(otpParameter.issuer ?: "2FAS", "UTF-8")

        val otpLink = buildString {
            append("otpauth://totp/")
            append(accountName)
            append("?secret=$secretBase32")
            append("&issuer=$issuer")
        }

        Log.d("OtpMigration", "Generated OTPAuth Link: $otpLink")
        otpLink
    } catch (e: InvalidProtocolBufferException) {
        Log.e("OtpMigration", "Failed to parse OTP migration payload: ${e.message}")
        throw e
    } catch (e: Exception) {
        Log.e("OtpMigration", "Error: ${e.message}", e)
        throw e
    }
}