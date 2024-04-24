package uni.project.disco_orario_sveglia_20

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uni.project.disco_orario_sveglia_20.databinding.FragmentCameraBinding
import uni.project.disco_orario_sveglia_20.databinding.FragmentMyCarBinding
import uni.project.disco_orario_sveglia_20.map.MyCarFragment
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var _imageUriFlow = MutableStateFlow<Uri?>(null)
    private val imageUri = _imageUriFlow.asStateFlow()

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){

        binding.imageView.setImageURI(imageUri.value)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        binding.captureButton.setOnClickListener {
            contract.launch(imageUri.value)
        }
        _imageUriFlow.update { createImageUri() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun createImageUri() : Uri {
        val image = File((activity as ParkingDataActivity).filesDir, "camera_photos.png")
        return FileProvider.getUriForFile((activity as ParkingDataActivity),
            "uni.project.disco_orario_sveglia_20.FileProvider",
            image)
    }


}