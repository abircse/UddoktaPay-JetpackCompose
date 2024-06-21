package com.app.uddoktapayjetpackcompose

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.uddoktapayjetpackcompose.helper.BackPressedHandler
import com.app.uddoktapayjetpackcompose.ui.theme.UddoktaPayJetpackComposeTheme
import com.help5g.uddoktapaysdk.UddoktaPay
import com.help5g.uddoktapaysdk.UddoktaPay.PaymentCallback

/**
 * File: MainActivity.kt
 * Author: Nayeem Shiddiki Abir
 * Date: 21th June, 2024
 * Description: A Customized Implementation of Uddukta Pay (A Payment gateway in bangladesh) in Android Jetpack compose
 * Version: 1.0
 * Source of Library: https://github.com/UddoktaPay/UddoktaPayAndroidSDK
 *
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UddoktaPayJetpackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    PaymentUI()
                }
            }
        }
    }
}

@Composable
fun PaymentUI(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    /**  Important Credentials N:B: KEEPING HERE FOR BEGINNER UNDERSTANDING SUGGESTIVE TO KEEP IN GRADLE OR ENV FILE **/
    val apiKey = "982d381360a69d419689740d9f2e26ce36fb7a50"

    /** This is a test Credential **/
    val checkoutUrl = "https://sandbox.uddoktapay.com/api/checkout-v2"
    val verifyPaymentUrl = "https://sandbox.uddoktapay.com/api/verify-payment"
    val redirectUrl = "https://your-site.com"
    val cancelUrl = "https://your-site.com"

    /** For Handle UI **/
    var isPaymentFieldVisible by remember { mutableStateOf(true) }
    var isPaymentWebViewVisible by remember { mutableStateOf(false) }
    var isResultShowing by remember { mutableStateOf(false) }

    /** For Store Data From User Input Panel **/
    var storedFullName by remember { mutableStateOf("") }
    var storedEmail by remember { mutableStateOf("") }
    var storedAmount by remember { mutableStateOf("") }

    /** For Show Response Result **/
    var storedTranSectionId by remember { mutableStateOf("") }
    var storedInvoiceId by remember { mutableStateOf("") }
    var storedPaymentMethod by remember { mutableStateOf("") }
    var storedDate by remember { mutableStateOf("") }

    Column(
        modifier
            .fillMaxSize()
            .padding(40.dp)) {

        /**
         * Load Input Panel in UI
         *
         * **/
        if (isPaymentFieldVisible) {

            Spacer(modifier.height(30.dp))

            Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier.fillMaxWidth())

            Spacer(modifier.height(30.dp))

            Text(text = "Enter Your Name")

            Spacer(modifier.height(10.dp))

            OutlinedTextField(
                value = storedFullName,
                onValueChange = {
                    storedFullName = it
                },
                placeholder = {
                    Text(text = "Write Here", fontSize = 15.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier.height(10.dp))

            Text(text = "Enter Your Email")

            Spacer(modifier.height(10.dp))

            OutlinedTextField(
                value = storedEmail,
                onValueChange = {
                    storedEmail = it
                },
                placeholder = {
                    Text(text = "Write Here", fontSize = 15.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier.height(10.dp))

            Text(text = "Enter Amount")

            Spacer(modifier.height(10.dp))

            OutlinedTextField(
                value = storedAmount,
                onValueChange = {
                    storedAmount = it
                },
                placeholder = {
                    Text(text = "Write Here", fontSize = 15.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        storedFullName.isEmpty() -> {
                            Toast.makeText(context, "Please Enter Valid Name", Toast.LENGTH_SHORT)
                                .show()
                        }

                        storedEmail.isEmpty() -> {
                            Toast.makeText(context, "Please Enter Valid Email", Toast.LENGTH_SHORT)
                                .show()
                        }

                        storedAmount.isEmpty() -> {
                            Toast.makeText(context, "Please Enter Valid Amount", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {
                            isPaymentFieldVisible = false
                            isPaymentWebViewVisible = true
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Text(
                    text = "Pay Now",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            /** ===  SHOW RESPONSE RESULT  ===
             *
             * You can get data from callback here
             * Here is the response data that providing by callback for user acknowledgement
             * status,fullName,email,amount,invoiceId,paymentMethod,senderNumber,transactionId, date, metadatavarues, fee, chargeAmount
             *
             *
             * */
            if (isResultShowing) {
                Text(text = "\n\nRESULT FROM PAYMENT API", fontSize = 20.sp)
                Spacer(modifier.height(20.dp))
                Text(text = "TransectionId: ${storedTranSectionId}\nInvoiceId: ${storedInvoiceId}\nPaymentMethod: ${storedPaymentMethod}\nDate: ${storedDate}")
            }
        }
    }

    /**
     *  === Load WebView in UI ===
     *
     * **/
    if (isPaymentWebViewVisible) {
        Column(Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                    }
                },
                update = { webView ->

                    /** ===  Initialize UddoktaPay SDK CallBack For Process Payment === **/
                    val paymentCallback =
                        PaymentCallback { status, fullName, email, amount, invoiceId, paymentMethod, senderNumber, transactionId, date, metadatavarues, fee, chargeAmount ->
                            /**
                             *  ===  Handle payment callback here ===
                             *
                             * Note: You can received Rest Payment Response from
                             * UddoktaPay SDK here which are viewing in callback
                             * lambda expression
                             *
                             * **/

                            /** Set data to Global Variable for later use **/
                            storedFullName = fullName
                            storedEmail = email
                            storedAmount = amount
                            storedTranSectionId = transactionId
                            storedInvoiceId = invoiceId
                            storedPaymentMethod = paymentMethod
                            storedDate = date

                            /** ===  Update UI based on payment status ===  **/
                            when (status) {
                                "COMPLETED" -> {
                                    isPaymentFieldVisible = true
                                    isPaymentWebViewVisible = false
                                    isResultShowing = true
                                }

                                "PENDING" -> {
                                    isPaymentFieldVisible = true
                                    isPaymentWebViewVisible = false
                                    isResultShowing = false
                                }

                                "ERROR" -> {
                                    isPaymentFieldVisible = true
                                    isPaymentWebViewVisible = false
                                    isResultShowing = false
                                }
                            }
                        }

                    /** Send data to Uddokta Pay SDK WebUrl & load it to our webView instance with a callback **/
                    val uddoktapay = UddoktaPay(webView, paymentCallback)
                    uddoktapay.loadPaymentForm(
                        apiKey,
                        storedFullName,
                        storedEmail,
                        storedAmount,
                        checkoutUrl,
                        verifyPaymentUrl,
                        redirectUrl,
                        cancelUrl,
                        hashMapOf()
                        /** You can add addition data as meta data here if needed **/
                    )
                }
            )
        }
    }

    /** ===  Handle back pressed action to go back to Payment Field ===  **/
    BackPressedHandler {
        isPaymentWebViewVisible = false
        isPaymentFieldVisible = true
        isResultShowing = false
    }
}