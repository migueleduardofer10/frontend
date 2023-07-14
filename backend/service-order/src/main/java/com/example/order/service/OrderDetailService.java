package com.example.order.service;

import com.example.order.client.CartClient;
import com.example.order.client.ProductClient;
import com.example.order.client.UserClient;
import com.example.order.dto.OrderInput;
import com.example.order.dto.OrderProductQuantity;
import com.example.order.dto.TransactionDetails;
import com.example.order.entity.OrderDetail;
import com.example.order.model.Cart;
import com.example.order.model.Product;
import com.example.order.model.User;
import com.example.order.repository.OrderDetailDao;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
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
    UserClient userClient;

    @Autowired
    ProductClient productClient;

    @Autowired
    CartClient cartClient;


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

    public List<OrderDetail> getOrderDetails(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        User userResponse = userClient.getUserByUsername(userName);
        if (userResponse == null) {
            throw new IllegalArgumentException("User not found");
        }
        String user = userResponse.getUserName();

        return orderDetailDao.findByUserName(user);
    }

    public void placeOrder(String userName, OrderInput orderInput, boolean isSingleProductCheckout) {
        List<OrderProductQuantity> productQuantityList = orderInput.getOrderProductQuantityList();

        for (OrderProductQuantity o: productQuantityList) {
            Product product = productClient.getProductById(o.getProductId());

            if (userName == null || userName.isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            User userResponse = userClient.getUserByUsername(userName);
            if (userResponse == null) {
                throw new IllegalArgumentException("User not found");
            }
            String user = userResponse.getUserName();

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

