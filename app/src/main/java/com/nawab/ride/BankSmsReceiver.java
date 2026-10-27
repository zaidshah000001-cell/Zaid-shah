package com.nawab.ride;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.regex.*;
import java.security.MessageDigest;

public class BankSmsReceiver extends BroadcastReceiver {
 public void onReceive(Context context,Intent intent){
  if(!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()))return;
  SharedPreferences prefs=context.getSharedPreferences("sms",Context.MODE_PRIVATE);
  if(!prefs.getBoolean("enabled",false))return;
  SmsMessage[] parts=Telephony.Sms.Intents.getMessagesFromIntent(intent);if(parts==null||parts.length==0)return;
  StringBuilder body=new StringBuilder();for(SmsMessage msg:parts)body.append(msg.getMessageBody());
  String sender=parts[0].getDisplayOriginatingAddress();String msg=body.toString();String lower=msg.toLowerCase(java.util.Locale.ROOT);
  // Strict bank transaction clues; no OTP or generic payment-request messages.
  if(lower.matches("(?s).*(otp|one.time.password|verification code|do not share|payment request|collect request).*"))return;
  boolean credit=lower.matches("(?s).*(credited|credit to|deposited|received|cr\\b).*"), debit=lower.matches("(?s).*(debited|debit from|spent|withdrawn|dr\\b).*" );
  if(credit==debit)return;
  Matcher amount=Pattern.compile("(?i)(?:inr|rs\\.?|₹)\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)").matcher(msg);
  if(!amount.find())return;
  double value;try{value=Double.parseDouble(amount.group(1).replace(",",""));}catch(Exception e){return;}if(value<=0||value>100000000)return;
  try{
   String hash=hash(sender+"|"+msg+"|"+parts[0].getTimestampMillis());
   JSONArray list=new JSONArray(prefs.getString("pending","[]"));for(int i=0;i<list.length();i++)if(hash.equals(list.getJSONObject(i).optString("id")))return;
   JSONObject item=new JSONObject();item.put("id",hash);item.put("type",credit?"income":"expense");item.put("amount",value);item.put("date",new java.text.SimpleDateFormat("yyyy-MM-dd",java.util.Locale.US).format(new java.util.Date(parts[0].getTimestampMillis())));item.put("sender",sender==null?"Bank":sender);list.put(item);
   while(list.length()>100){JSONArray trimmed=new JSONArray();for(int i=1;i<list.length();i++)trimmed.put(list.get(i));list=trimmed;}
   prefs.edit().putString("pending",list.toString()).apply();
  }catch(Exception ignored){}
 }
 private static String hash(String input)throws Exception{byte[] b=MessageDigest.getInstance("SHA-256").digest(input.getBytes("UTF-8"));StringBuilder out=new StringBuilder();for(byte x:b)out.append(String.format("%02x",x&255));return out.toString();}
}
