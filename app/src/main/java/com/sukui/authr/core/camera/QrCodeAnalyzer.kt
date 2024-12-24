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
                    val uri = result.text // Log the URI that was decoded
                    Log.d("QR_SCAN", "Decoded URI: $uri")

                    if (uri.startsWith("otpauth://")) {
                        onSuccess(result)
                    } else if (uri.startsWith("otpauth-migration://")) {
                        val uriParts = uri.split("=", limit = 2)
                        val base = uriParts[1]
                        Log.d("Base", "Base: $base")
                        decodeOtpAuthMigrationLink(uri)
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

    private fun decodeOtpAuthMigrationLink(link: String) {
        try {
            // Step 1: URL Decode
            val decodedLink = URLDecoder.decode(link, "UTF-8")
            Log.d("DecodedLink", "URL Decoded: $decodedLink")

            // Step 2: Extract Base64 Data
            val base64Data = decodedLink.split("=", limit = 2).getOrNull(1)
                ?: throw IllegalArgumentException("Invalid OTP migration link: No data parameter found")

            // Step 3: Base64 Decode
            val decodedData = Base64.decode(base64Data, Base64.DEFAULT)

            // Step 4: Parse Protobuf Payload
            val otpMigrationPayload = Payload.parseFrom(decodedData)

            // Step 5: Log the Parsed Data
            Log.d("OtpMigration", "Version: ${otpMigrationPayload.version}")
            Log.d("OtpMigration", "Batch Size: ${otpMigrationPayload.batchSize}")
            Log.d("OtpMigration", "Batch Index: ${otpMigrationPayload.batchIndex}")
            Log.d("OtpMigration", "Batch ID: ${otpMigrationPayload.batchId}")
            Log.d("OtpMigration", "---- OTP Parameters ----")

            otpMigrationPayload.otpParametersList.forEachIndexed { index, otpParameter ->
                Log.d("OtpMigration", "Account #${index + 1}")
                Log.d("OtpMigration", "Name: ${otpParameter.name}")
                Log.d("OtpMigration", "Issuer: ${otpParameter.issuer}")
                Log.d("OtpMigration", "Raw Secret: ${otpParameter.secret}")
                val secretBytes = otpParameter.secret.toByteArray()
                val base32 = Base32()
                val secretBase32 = base32.encodeToString(secretBytes)
                Log.d("OtpMigration", "Decoded Secret (Base32): $secretBase32")
                Log.d("OtpMigration", "Algorithm: ${otpParameter.algorithm}")
                Log.d("OtpMigration", "Digits: ${otpParameter.digits}")
                Log.d("OtpMigration", "Type: ${otpParameter.type}")

                if (otpParameter.type.getNumber() == Payload.OtpType.OTP_TYPE_HOTP_VALUE) {
                    Log.d("OtpMigration", "Counter: ${otpParameter.counter}")
                }

                Log.d("OtpMigration", "------------------------")
            }
        } catch (e: InvalidProtocolBufferException) {
            Log.e("OtpMigration", "Failed to parse OTP migration payload: ${e.message}")
        } catch (e: Exception) {
            Log.e("OtpMigration", "Error: ${e.message}", e)
        }
    }

}