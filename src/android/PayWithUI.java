import android.app.Activity;
import android.content.Context;

import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.model.SplitSettlement;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.android.inapp.Pay;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.inapp.PayWithToken;
import com.interswitchng.sdk.payment.android.inapp.PayWithWallet;
import com.interswitchng.sdk.payment.android.inapp.ValidateCard;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.payment.model.ValidateCardResponse;
import com.interswitchng.sdk.payment.model.AuthorizeCardRequest;
import com.interswitchng.sdk.payment.model.AuthorizeCardResponse;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PayWithUI extends CordovaPlugin{
    private String clientId;
    private String clientSecret;
    private static RequestOptions options;
    private Activity activity;
    private Context context;

    public PayWithUI(Activity activity, String clientId, String clientSecret ){
        this.activity = activity;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity =  cordova.getActivity();
    }

    public void pay(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException{
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    options = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret).build();
                    JSONObject params = args.getJSONObject(0);
                    String customerId = params.getString("customerId");
                    String currency = params.getString("currency");
                    String description = params.getString("description");
                    String amount = params.getString("amount");
                    Pay pay = new Pay(activity, customerId, description, amount, currency, options, new SerializablePaymentCallback<PurchaseResponse>(new IswCallback<PurchaseResponse>() {
                        @Override
                        public void onError(Exception error) {
                            callbackContext.error(error.getMessage());
                        }

                        @Override
                        public void onSuccess(PurchaseResponse response) {
                            PluginUtils.getPluginResult(callbackContext, response);
                        }
                    }));
                    pay.start();
                } catch (Exception ex) {
                    callbackContext.error(ex.toString());
                }
            }
        });
    }
    public void payWithCard(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException{
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    RequestOptions.RequestOptionsBuilder builder = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret);
                    JSONObject params = args.getJSONObject(0);
                    String customerId = params.getString("customerId");
                    String currency = params.getString("currency");
                    String description = params.getString("description");
                    String thirdPartyTransferType = null;
                    if (params.has("thirdPartyTransactionType")) {
                        thirdPartyTransferType = params.getString("thirdPartyTransactionType");
                    }
                    String amount = params.getString("amount");
                    List<SplitSettlement> split = new ArrayList<SplitSettlement>();
                    if (params.has("splitSettlement")) {
                        JSONArray splitSettlementArray = params.getJSONArray("splitSettlement");
                        if (splitSettlementArray != null && splitSettlementArray.length() != 0) {
                            for (int i = 0; i < splitSettlementArray.length(); i++) {
                                JSONObject splitObject = splitSettlementArray.getJSONObject(i);
                                SplitSettlement splitToAdd = new SplitSettlement();
                                splitToAdd.setAccountIdentifier(splitObject.getString("accountIdentifier"));
                                splitToAdd.setAmount(splitObject.getString("amount"));
                                split.add(splitToAdd);
                            }
                        }
                    }
                    if (!split.isEmpty()) {
                        builder.setSplitSettlementInformation(split.toArray(new SplitSettlement[split.size()]));
                    }
                    options = builder.build();
                    String transactionRef = null;
                    String KEY_TRANSACTION_REF = "transactionRef";
                    if(params.has(KEY_TRANSACTION_REF)) {
                        transactionRef=params.getString(KEY_TRANSACTION_REF);
                    }
                    PayWithCard pay;
                    if(transactionRef != null && !transactionRef.isEmpty()) {
                        pay = new PayWithCard(activity, customerId, description, amount, currency, transactionRef, options, new SerializablePaymentCallback<PurchaseResponse>(new IswCallback<PurchaseResponse>() {
                            @Override
                            public void onError(Exception error) {
                                callbackContext.error(error.getMessage());
                            }

                            @Override
                            public void onSuccess(PurchaseResponse response) {
                                PluginUtils.getPluginResult(callbackContext, response);
                            }
                        }), false, thirdPartyTransferType);
                    }else{
                        pay = new PayWithCard(activity, customerId, description, amount, currency, options, new SerializablePaymentCallback<PurchaseResponse>(new IswCallback<PurchaseResponse>() {
                            @Override
                            public void onError(Exception error) {
                                callbackContext.error(error.getMessage());
                            }

                            @Override
                            public void onSuccess(PurchaseResponse response) {
                                PluginUtils.getPluginResult(callbackContext, response);
                            }
                        }), false, thirdPartyTransferType);
                    }
                    pay.start();
                } catch (Exception ex) {
                    callbackContext.error(ex.toString());
                }
            }
        });
    }
    public void payWithWallet(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException{
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    options = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret).build();
                    JSONObject params = args.getJSONObject(0);
                    String customerId = params.getString("customerId");
                    String currency = params.getString("currency");
                    String description = params.getString("description");
                    String amount = params.getString("amount");
                    PayWithWallet payWithWallet = new PayWithWallet(activity, customerId, description, amount, currency, options, new SerializablePaymentCallback<PurchaseResponse>(new IswCallback<PurchaseResponse>() {
                        @Override
                        public void onError(Exception error) {
                            callbackContext.error(error.getMessage());
                        }

                        @Override
                        public void onSuccess(PurchaseResponse response) {
                            PluginUtils.getPluginResult(callbackContext, response);
                        }
                    }));
                    payWithWallet.start();
                } catch (Exception ex) {
                    callbackContext.error(ex.toString());
                }
            }
        });
    }
    public void payWithToken(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException{
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    options = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret).build();
                    JSONObject params = args.getJSONObject(0);
                    String currency = params.getString("currency");
                    String token = params.getString("pan");
                    String amount = params.getString("amount");
                    String cardType = params.getString("cardtype");
                    String panLast4Digits = params.getString("panLast4Digits");
                    String expiryDate = params.getString("expiryDate");
                    String customerId = params.getString("customerId");
                    String description = params.getString("description");
                    PayWithToken payWithToken = new PayWithToken(activity, customerId, amount, token, expiryDate, currency, cardType, panLast4Digits, description, options,
                            new SerializablePaymentCallback<PurchaseResponse>(new IswCallback<PurchaseResponse>() {
                                @Override
                                public void onError(Exception error) {
                                    callbackContext.error(error.getMessage());
                                }

                                @Override
                                public void onSuccess(PurchaseResponse response) {
                                    PluginUtils.getPluginResult(callbackContext, response);
                                }
                            }));
                    payWithToken.start();
                } catch (Exception ex) {
                    callbackContext.error(ex.toString());
                }
            }
        });
    }
    public void validatePaymentCard(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException{
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    JSONObject params = args.getJSONObject(0);
                    String customerId = params.getString("customerId");
                    options = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret).build();
                    ValidateCard validateCard = new ValidateCard(activity, customerId, options, new SerializablePaymentCallback<AuthorizeCardResponse>(new IswCallback<AuthorizeCardResponse>() {
                        @Override
                        public void onError(Exception error) {
                            callbackContext.error(error.getMessage());
                        }

                        @Override
                        public void onSuccess(AuthorizeCardResponse response) {
                            PluginUtils.getPluginResult(callbackContext, response);
                        }
                    }));
                    validateCard.start();
                } catch (Exception error) {
                    callbackContext.error(error.toString());
                }
            }
        });
    }
}