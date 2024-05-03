package uni.project.disco_orario_sveglia_20.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import coil.load
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.databinding.FragmentCameraBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        viewModel = (activity as ParkingDataActivity).parkingViewModel

        binding.captureButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    (activity as ParkingDataActivity),
                    Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
                ) {
                viewModel.getCameraPermission((activity as ParkingDataActivity))
                viewModel.deleteImageFile((activity as ParkingDataActivity))
                contract.launch(viewModel.createImageUri((activity as ParkingDataActivity)))
            } else {
                viewModel.deleteImageFile((activity as ParkingDataActivity))
                contract.launch(viewModel.createImageUri((activity as ParkingDataActivity)))
            }

        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onResume() {
        super.onResume()
        binding.imageView.setImageDrawable(null)
        viewModel.getImageFile((activity as ParkingDataActivity))?.let {
            binding.imageView.load(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        contract.unregister()
    }


}