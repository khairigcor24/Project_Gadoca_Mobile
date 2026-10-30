package com.example.project_farhangadoca_mobilee

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityOrderBinding
import com.example.project_farhangadoca_mobilee.databinding.ItemOrderBinding
import java.text.NumberFormat
import java.util.Locale

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderRepository: OrderRepository
    private var productName: String = ""
    private var productPrice: Int = 0
    private var productDescription: String = ""
    private var productImageResId: Int = 0
    private var checkoutTableId: String? = null
    private val selectedOrderIds = mutableSetOf<String>()
    private var pendingConfirmationIds: Set<String> = emptySet()
    private val orderStatusNotifier by lazy { OrderStatusNotifier(this) }
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val orderIds = pendingConfirmationIds
            pendingConfirmationIds = emptySet()
            completeSelectedOrders(orderIds, granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        orderRepository = OrderRepository(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        productName = intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty()
        productDescription = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION).orEmpty()
        productPrice = intent.getIntExtra(EXTRA_PRODUCT_PRICE, 0)
        productImageResId = intent.getIntExtra(EXTRA_PRODUCT_IMAGE, 0)
        checkoutTableId = orderRepository.getSelectedTableId()

        val hasOrder = productName.isNotBlank() && productPrice > 0
        if (hasOrder) {
            showCheckout()
        } else {
            showOrderHistory()
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnConfirmOrder.setOnClickListener { confirmOrder() }
        binding.btnDeleteSelected.setOnClickListener { confirmDeleteSelected() }
        binding.btnConfirmSelected.setOnClickListener { confirmSelectedOrders() }
        binding.btnBrowseMenu.setOnClickListener {
            openActivity(MenuActivity::class.java)
        }
        binding.tabBeranda.setOnClickListener { openActivity(BerandaActivity::class.java) }
        binding.tabMenu.setOnClickListener { openActivity(MenuActivity::class.java) }
        binding.tabAkun.setOnClickListener { openActivity(DeveloperProfileActivity::class.java) }
    }

    private fun showCheckout() {
        binding.tvOrderPageTitle.text = "Konfirmasi Pesanan"
        binding.checkoutContent.visibility = View.VISIBLE
        binding.btnConfirmOrder.visibility = View.VISIBLE
        binding.ordersContent.visibility = View.GONE
        binding.emptyOrderState.visibility = View.GONE
        binding.orderActions.visibility = View.GONE

        binding.tvOrderProductName.text = productName
        binding.tvOrderProductDescription.text = productDescription
        binding.tvOrderProductPrice.text = formatRupiah(productPrice)
        binding.tvOrderTotal.text = formatRupiah(productPrice)
        binding.tvOrderTable.text = checkoutTableId?.let { "Meja $it" }
            ?: "Belum memilih meja (opsional)"
        if (productImageResId != 0) {
            binding.ivOrderProduct.setImageResource(productImageResId)
        }
    }

    private fun showOrderHistory(newOrderId: String? = null) {
        binding.tvOrderPageTitle.text = "Pesanan"
        binding.checkoutContent.visibility = View.GONE
        binding.btnConfirmOrder.visibility = View.GONE
        binding.ordersContent.visibility = View.VISIBLE
        binding.emptyOrderState.visibility = View.GONE
        selectedOrderIds.clear()
        binding.tvNewOrderNotice.visibility = if (newOrderId == null) View.GONE else View.VISIBLE
        binding.orderListContainer.removeAllViews()

        val orders = orderRepository.getOrders()
        if (orders.isEmpty()) {
            binding.ordersContent.visibility = View.GONE
            binding.emptyOrderState.visibility = View.VISIBLE
            binding.orderActions.visibility = View.GONE
            return
        }
        binding.orderActions.visibility = View.VISIBLE

        val inflater = LayoutInflater.from(this)
        orders.forEach { order ->
            val orderBinding = ItemOrderBinding.inflate(inflater, binding.orderListContainer, false)
            orderBinding.tvHistoryProductName.text = order.productName
            orderBinding.tvHistoryProductDescription.text = order.description
            orderBinding.tvHistoryProductPrice.text = formatRupiah(order.price)
            orderBinding.tvHistoryPayment.text = "Pembayaran: ${order.paymentMethod}"
            orderBinding.tvHistoryStatus.text = order.status
            orderBinding.tvHistoryTable.text = order.tableId?.let { "Meja: $it" }.orEmpty()
            orderBinding.tvHistoryTable.visibility =
                if (order.tableId == null) View.GONE else View.VISIBLE
            orderBinding.cbSelectOrder.isChecked = order.id in selectedOrderIds
            orderBinding.cbSelectOrder.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedOrderIds.add(order.id) else selectedOrderIds.remove(order.id)
                updateOrderActionButtons(orders)
            }
            if (order.imageResId != 0) {
                orderBinding.ivHistoryProduct.setImageResource(order.imageResId)
            }
            binding.orderListContainer.addView(orderBinding.root)
        }
        binding.tvNewOrderNotice.text = if (newOrderId == null) {
            ""
        } else {
            "Pesanan baru berhasil dikonfirmasi."
        }
        updateOrderActionButtons(orders)
    }

    private fun updateOrderActionButtons(orders: List<OrderRecord>) {
        val hasSelection = selectedOrderIds.isNotEmpty()
        binding.btnDeleteSelected.isEnabled = hasSelection
        binding.btnConfirmSelected.isEnabled = orders.any {
            it.id in selectedOrderIds && it.status != OrderRepository.STATUS_CONFIRMED
        }
    }

    private fun confirmDeleteSelected() {
        if (selectedOrderIds.isEmpty()) return
        AlertDialog.Builder(this)
            .setTitle("Hapus pesanan?")
            .setMessage("Pesanan yang dipilih akan dihapus dari daftar.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                val count = selectedOrderIds.size
                orderRepository.deleteOrders(selectedOrderIds.toSet())
                selectedOrderIds.clear()
                showOrderHistory()
                Toast.makeText(this, "$count pesanan dihapus.", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun confirmSelectedOrders() {
        val ordersToConfirm = orderRepository.getOrders()
            .filter { it.id in selectedOrderIds && it.status != OrderRepository.STATUS_CONFIRMED }
            .map { it.id }
            .toSet()
        if (ordersToConfirm.isEmpty()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pendingConfirmationIds = ordersToConfirm
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        completeSelectedOrders(ordersToConfirm, notificationsAllowed = true)
    }

    private fun completeSelectedOrders(orderIds: Set<String>, notificationsAllowed: Boolean) {
        if (orderIds.isEmpty()) return
        val changedOrders = orderRepository.confirmOrders(orderIds)
        selectedOrderIds.clear()
        showOrderHistory()
        Toast.makeText(
            this,
            if (notificationsAllowed) {
                "${changedOrders.size} pesanan dikonfirmasi."
            } else {
                "Status pesanan diperbarui, tetapi izin notifikasi tidak diberikan."
            },
            Toast.LENGTH_SHORT
        ).show()
        if (notificationsAllowed) {
            changedOrders.forEach(orderStatusNotifier::showStatusChanged)
        }
    }

    private fun openActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
        finish()
    }

    private fun confirmOrder() {
        val selectedPaymentId = binding.rgPaymentMethods.checkedRadioButtonId
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Pilih metode pembayaran terlebih dahulu.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val paymentMethod = when (selectedPaymentId) {
            R.id.rbCash -> binding.rbCash.text.toString()
            R.id.rbQris -> binding.rbQris.text.toString()
            R.id.rbBankTransfer -> binding.rbBankTransfer.text.toString()
            else -> {
                Toast.makeText(
                    this,
                    "Pilih metode pembayaran terlebih dahulu.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }
        val savedOrder = orderRepository.addOrder(
            productName = productName,
            description = productDescription,
            price = productPrice,
            imageResId = productImageResId,
            paymentMethod = paymentMethod,
            tableId = checkoutTableId
        )
        showOrderHistory(savedOrder.id)
        Toast.makeText(this, "Pesanan baru berhasil dikonfirmasi.", Toast.LENGTH_SHORT).show()
    }

    private fun formatRupiah(amount: Int): String =
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(amount)

    companion object {
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_IMAGE = "extra_product_image"
    }
}
