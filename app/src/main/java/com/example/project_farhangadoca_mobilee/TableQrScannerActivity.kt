package com.example.project_farhangadoca_mobilee

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityTableQrScannerBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class TableQrScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTableQrScannerBinding
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val resultHandled = AtomicBoolean(false)
    private val scanFailureReported = AtomicBoolean(false)
    private val barcodeScannerDelegate = lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }
    private val barcodeScanner: BarcodeScanner by barcodeScannerDelegate
    private var cameraProvider: ProcessCameraProvider? = null
    private var permissionDenied = false

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                permissionDenied = false
                startCamera()
            } else {
                permissionDenied = true
                showMessage(
                    "Izin kamera diperlukan untuk memindai QR meja.",
                    showRetry = true
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarScanner.setNavigationOnClickListener { finish() }
        binding.btnRetryScan.setOnClickListener { retryScan() }
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
            return
        }

        if (permissionDenied && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(this)
                .setTitle("Izin kamera dibutuhkan")
                .setMessage("Aktifkan izin kamera Gadoca dari Pengaturan untuk memindai QR meja.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Pengaturan") { _, _ ->
                    val settingsIntent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    )
                    startActivity(settingsIntent)
                }
                .show()
            return
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(this)
                .setTitle("Izin kamera")
                .setMessage("Kamera hanya digunakan untuk membaca QR Code meja.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Lanjutkan") { _, _ ->
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                .show()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        binding.tvScannerMessage.text = "Arahkan kamera ke QR Code meja."
        binding.btnRetryScan.visibility = View.GONE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = binding.previewView.surfaceProvider
                }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { imageAnalysis ->
                        imageAnalysis.setAnalyzer(cameraExecutor, ::analyzeImage)
                    }

                provider.unbindAll()
                provider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            } catch (exception: ExecutionException) {
                showCameraError(exception)
            } catch (exception: InterruptedException) {
                Thread.currentThread().interrupt()
                showCameraError(exception)
            } catch (exception: IllegalArgumentException) {
                showCameraError(exception)
            } catch (exception: IllegalStateException) {
                showCameraError(exception)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeImage(imageProxy: androidx.camera.core.ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val qrCode = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                if (qrCode != null && resultHandled.compareAndSet(false, true)) {
                    if (!isFinishing && !isDestroyed) {
                        runOnUiThread { handleQrResult(qrCode.rawValue) }
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (scanFailureReported.compareAndSet(false, true)) {
                    Log.e(TAG, "QR Code tidak dapat diproses.", exception)
                    if (!isFinishing && !isDestroyed) {
                        runOnUiThread {
                            showMessage(
                                "QR tidak dapat dibaca. Coba pindai ulang.",
                                showRetry = true
                            )
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun handleQrResult(rawValue: String?) {
        val tableId = TableQrCode.parse(rawValue)
        if (tableId == null) {
            showMessage(
                "QR tidak valid. Gunakan format GADOCA:TABLE:<identitas meja>.",
                showRetry = true
            )
            return
        }

        setResult(
            RESULT_OK,
            Intent().putExtra(EXTRA_TABLE_ID, tableId)
        )
        finish()
    }

    private fun retryScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
            return
        }
        if (cameraProvider == null) {
            startCamera()
            return
        }
        resultHandled.set(false)
        scanFailureReported.set(false)
        binding.tvScannerMessage.text = "Arahkan kamera ke QR Code meja."
        binding.btnRetryScan.visibility = View.GONE
    }

    private fun showCameraError(exception: Exception) {
        Log.e(TAG, "Kamera tidak dapat dibuka.", exception)
        cameraProvider = null
        showMessage("Kamera tidak tersedia. Periksa izin dan coba lagi.", showRetry = true)
    }

    private fun showMessage(message: String, showRetry: Boolean) {
        binding.tvScannerMessage.text = message
        binding.btnRetryScan.visibility = if (showRetry) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        cameraProvider?.unbindAll()
        if (barcodeScannerDelegate.isInitialized()) barcodeScanner.close()
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_TABLE_ID = "extra_table_id"
        private const val TAG = "TableQrScanner"
    }
}
