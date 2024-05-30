package uni.project.disco_orario_sveglia_20.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var loadingDialog: Dialog

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        val activity = activity as ParkingDataActivity
        viewModel = activity.parkingViewModel

        binding.captureButton.setOnClickListener {
            if (viewModel.isCameraPermissionOk(activity))
            {
                contract.launch(viewModel.createImageUri(activity))
            } else {
                viewModel.getCameraPermission(activity)
                showToast(R.string.camera_permission,activity)
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as ParkingDataActivity
        binding.imageView.setImageDrawable(null)
        viewModel.getImageFile(activity)?.let {
            showLoadingDialog()
            binding.imageView.load(it) {
                listener(
                    onSuccess = { _, _ -> dismissLoadingDialog() },
                    onError = { _, _ ->
                        dismissLoadingDialog()
                        showToast(R.string.generic_error, activity)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contract.unregister()
        cameraExecutor.shutdown()
    }

    private fun showLoadingDialog() {
        val activity = activity as ParkingDataActivity
        loadingDialog = Dialog(activity)
        loadingDialog.apply {
            setContentView(R.layout.loading_dialog)
            setCancelable(false)
            show()
        }
    }

    private fun dismissLoadingDialog() {
        loadingDialog.takeIf { it.isShowing }?.dismiss()
    }

    private fun showToast(messageResId: Int, context: Context) {
        Toast.makeText(context, messageResId, Toast.LENGTH_LONG).show()
    }

}