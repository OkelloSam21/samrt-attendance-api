package com.smartattendance.android.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

/**
 * Utility class for QR code generation
 */
object QrCodeUtil {
    /**
     * Generate a QR code bitmap from content
     * 
     * @param content The text content to encode in the QR code
     * @param size The width and height of the QR code in pixels
     * @return A bitmap containing the QR code
     */
    fun generateQrCodeBitmap(content: String, size: Int = 512): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 1 // Make the QR code a bit more compact
        hints[EncodeHintType.ERROR_CORRECTION] = com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M // Medium error correction
        
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        
        return bitmap
    }
    
    /**
     * Generate a QR code with a logo in the center
     * 
     * @param content The text content to encode in the QR code
     * @param logo The logo bitmap to place in the center
     * @param size The width and height of the QR code in pixels
     * @return A bitmap containing the QR code with the logo
     */
    fun generateQrCodeWithLogo(content: String, logo: Bitmap, size: Int = 512): Bitmap {
        val qrBitmap = generateQrCodeBitmap(content, size)
        
        // Create a new bitmap to hold the combined image
        val combined = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(combined)
        
        // Draw the QR code first
        canvas.drawBitmap(qrBitmap, 0f, 0f, null)
        
        // Scale the logo to be about 20% of the QR code size
        val logoSize = size / 5
        val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
        
        // Calculate position to center the logo
        val logoX = (size - logoSize) / 2f
        val logoY = (size - logoSize) / 2f
        
        // Draw the logo on top of the QR code
        canvas.drawBitmap(scaledLogo, logoX, logoY, null)
        
        return combined
    }
    
    /**
     * Generate a colored QR code bitmap
     * 
     * @param content The text content to encode in the QR code
     * @param foregroundColor The color of the QR code dots
     * @param backgroundColor The background color of the QR code
     * @param size The width and height of the QR code in pixels
     * @return A bitmap containing the colored QR code
     */
    fun generateColoredQrCode(
        content: String,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        size: Int = 512
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 1
        
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) foregroundColor else backgroundColor)
            }
        }
        
        return bitmap
    }
}