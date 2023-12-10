import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.utils.KAlertDialogType

object PermissionUtils {

    private val permissions = arrayListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
    )
    private const val PERMISSION_REQUEST_CODE = 100

    fun checkPermissions(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT > 32)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        else
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                deniedPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            return false
        }

        return true
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        onDenied: () -> Unit = {}
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                onDenied()
            }
        }
    }

    fun getPermissionAlert(
        activity: AppCompatActivity,
        message: String,
        type: KAlertDialogType,
        onConfirmClick: KAlertDialog.KAlertClickListener
    ): KAlertDialog = KAlertDialog(activity, true).apply {
        changeAlertType(type.ordinal)
        setTitle(context.getString(R.string.permission_required))
        contentText = message
        setCancelable(false)
        setConfirmClickListener("OK", onConfirmClick)
    }
}
