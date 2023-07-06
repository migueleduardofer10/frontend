package com.youtube.ecommerce.service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.youtube.ecommerce.configuration.JwtRequestFilter;
import com.youtube.ecommerce.dao.CartDao;
import com.youtube.ecommerce.dao.OrderDetailDao;
import com.youtube.ecommerce.dao.ProductDao;
import com.youtube.ecommerce.dao.UserDao;
import com.youtube.ecommerce.dto.OrderInput;
import com.youtube.ecommerce.dto.OrderProductQuantity;
import com.youtube.ecommerce.dto.TransactionDetails;
import com.youtube.ecommerce.entity.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailService {

    private static final String ORDER_PLACED = "Placed";

    private static final String CLIENT_ID = "AXrQC6oTYxQ7lKkgmU0Q6vEZXcczAE2xRUULDSawrlStC3usul_VME8cXdmv7MrTY0E58WGivKpSKfWw";
    private static final String CLIENT_SECRET = "EIN05s3ebkdOopvYb8WcAexHxPS8k-9BvpFq7sDPEO3fSKVvEekdpEYciO3LQfD4VMWefJ_SnXwA2Zx_";
    private static final String MODE = "sandbox";  // cambiar a "live" cuando esté listo para el entorno de producción
    private static final String CURRENCY = "INR";

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CartDao cartDao;

    public List<OrderDetail> getAllOrderDetails(String status) {
        List<OrderDetail> orderDetails = new ArrayList<>();

        if(status.equals("All")) {
            orderDetailDao.findAll().forEach(
                    x -> orderDetails.add(x)
            );
        } else {
            orderDetailDao.findByOrderStatus(status).forEach(
                    x -> orderDetails.add(x)
            );
        }


         return orderDetails;
    }

    public List<OrderDetail> getOrderDetails() {
        String currentUser = JwtRequestFilter.CURRENT_USER;
        User user = userDao.findById(currentUser).get();

        return orderDetailDao.findByUser(user);
    }

    public void placeOrder(OrderInput orderInput, boolean isSingleProductCheckout) {
        List<OrderProductQuantity> productQuantityList = orderInput.getOrderProductQuantityList();

        for (OrderProductQuantity o: productQuantityList) {
            Product product = productDao.findById(o.getProductId()).get();

            String currentUser = JwtRequestFilter.CURRENT_USER;
            User user = userDao.findById(currentUser).get();

            OrderDetail orderDetail = new OrderDetail(
                  orderInput.getFullName(),
                  orderInput.getFullAddress(),
                  orderInput.getContactNumber(),
                  orderInput.getAlternateContactNumber(),
                    ORDER_PLACED,
                    product.getProductDiscountedPrice() * o.getQuantity(),
                    product,
                    user,
                    orderInput.getTransactionId()
            );

            // empty the cart.
            if(!isSingleProductCheckout) {
                List<Cart> carts = cartDao.findByUser(user);
                carts.stream().forEach(x -> cartDao.deleteById(x.getCartId()));
            }

            orderDetailDao.save(orderDetail);
        }
    }

    public void markOrderAsDelivered(Integer orderId) {
        OrderDetail orderDetail = orderDetailDao.findById(orderId).get();

        if(orderDetail != null) {
            orderDetail.setOrderStatus("Delivered");
            orderDetailDao.save(orderDetail);
        }

    }

    public TransactionDetails createTransaction(Double amount) {
        // Credentials for PayPal
        String clientId = "your_client_id";
        String clientSecret = "your_client_secret";

        // Creating an APIContext object
        APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");

        Amount amnt = new Amount();
        amnt.setCurrency("USD");
        amnt.setTotal(String.format("%.2f", amount));

        Transaction transaction = new Transaction();
        transaction.setAmount(amnt);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        try {
            Payment createdPayment = payment.create(apiContext);
            String paypalPaymentId = createdPayment.getId();
            // TODO: save the paymentId to the database for verification later

            TransactionDetails transactionDetails = new TransactionDetails(paypalPaymentId, "USD", (int)(amount * 100), clientId);
            return transactionDetails;

        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private TransactionDetails prepareTransactionDetails(Payment payment) {
        // Aquí adaptas el objeto Payment a tu clase TransactionDetails
        // Deberías modificar este método según tus necesidades.
        String paymentId = payment.getId();
        String currency = payment.getTransactions().get(0).getAmount().getCurrency();
        Integer amount = Integer.parseInt(payment.getTransactions().get(0).getAmount().getTotal());

        TransactionDetails transactionDetails = new TransactionDetails(paymentId, currency, amount, CLIENT_ID);
        return transactionDetails;
    }

}

