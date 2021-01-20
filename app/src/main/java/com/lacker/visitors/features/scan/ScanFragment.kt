package com.lacker.visitors.features.scan

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.core.view.children
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.github.florent37.runtimepermission.RuntimePermission
import com.lacker.utils.extensions.*
import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.base.VolumeKeysPressListener
import com.lacker.visitors.features.scan.ScanMachine.State
import com.lacker.visitors.features.scan.ScanMachine.Wish
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.android.synthetic.main.include_blocking_progress.*

class ScanFragment : ToolbarFluxFragment<Wish, State>(), VolumeKeysPressListener {

    companion object {
        fun newInstance() = ScanFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_scan

    override val machine by lazy { getMachineFromFactory(ScanMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.screenTitleScan),
            subtitle = null,
            showBackIcon = false,
            menuResId = R.menu.help_menu
        )
    }

    private val qrView: QRCodeReaderView
        get() = existingQrView ?: createAndInflateQrView()

    private val existingQrView: QRCodeReaderView?
        get() = scanLayout.children.filterIsInstance(QRCodeReaderView::class.java).firstOrNull()

    private fun createAndInflateQrView() = QRCodeReaderView(requireContext()).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        scanLayout.addView(this, 0)

        setOnQRCodeReadListener {
            performWish(Wish.Code(it))
            vibrate()
        }

        setOnClickListener { checkPermissionAndRunCamera() }

        setAutofocusInterval(200L)
    }

    override fun onScreenInit() {

        switchCamera.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> checkPermissionAndRunCamera()
                false -> performWish(Wish.DisableCamera)
            }
        }

        help.setOnClickListener { performWish(Wish.ToggleHelp(false)) }

        checkPermissionAndRunCamera()
    }

    override fun render(state: State) = with(state) {
        help.visible = showHelp

        if (switchCamera?.isChecked != cameraEnabled)
            switchCamera.isChecked = cameraEnabled

        if (cameraEnabled) {
            qrView.startCamera()
            qrView.setTorchEnabled(lightningEnabled)
        } else {
            existingQrView?.setTorchEnabled(false)
            existingQrView?.stopCamera()
        }

        blockingProgress.visible = state.loadingInProcess
    }

    override fun onResume() {
        super.onResume()
        if (hasPermission(Manifest.permission.CAMERA)) performWish(Wish.EnableCamera)
    }

    private fun checkPermissionAndRunCamera() {
        if (!hasPermission(Manifest.permission.CAMERA)) askPermission()
        else performWish(Wish.EnableCamera)
    }

    private fun askPermission() {
        try {
            RuntimePermission(activity)
                .request(Manifest.permission.CAMERA)
                .onAccepted { performWish(Wish.EnableCamera) }
                .onDenied {
                    Handler(Looper.getMainLooper()).postDelayed({ askPermission() }, 50L)
                }
                .onForeverDenied { onUserForeverDeniedPermission { askPermission() } }
                .ask()
        } catch (e: IllegalStateException) {
            Handler(Looper.getMainLooper()).postDelayed({ askPermission() }, 50L)
        }
    }

    override fun onPause() {
        super.onPause()
        existingQrView?.setTorchEnabled(false)
        performWish(Wish.DisableCamera)
    }

    override fun onVolumeUp() = performWish(Wish.EnableLightning)

    override fun onVolumeDown() = performWish(Wish.DisableLightning)

    override fun onMenuItemChosen(itemId: Int): Boolean {
        if (itemId == R.id.helpIcon) {
            performWish(Wish.ToggleHelp())
            return true
        }

        return false
    }
}