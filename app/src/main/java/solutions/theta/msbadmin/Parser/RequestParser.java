package solutions.theta.msbadmin.Parser;

import android.util.Log;

import org.json.JSONObject;

import solutions.theta.msbadmin.Classes.User;
import solutions.theta.msbadmin.Http.TaskResult;


/**
 * Created by SALAH UD DIN on 3/26/2016.
 */
public class RequestParser implements  BaseParser {
    User ouser;
    @Override
    public TaskResult parse(int httpCode, String response) {
        Log.d("Response",response);
        TaskResult result = new TaskResult();
        if(httpCode == SUCCESS) {
            // Parsing here
            try {
                JSONObject obj2 = new JSONObject(response);
                if(obj2.optInt("code")==200){
                    JSONObject obj =obj2.optJSONObject("response");
                    int userid = obj.optInt("Id");
                    String Username = obj.optString("Username");
                    String Mobilenumber = obj.optString("Mobilenumber");
                    String Addressline1 = obj.optString("Addressline1");
                    String City = obj.optString("City");
                    String Role = obj.optString("Role");
                    ouser=new User();
                    ouser.setId(userid);
                    ouser.setUsername(Username);
                    ouser.setMobilenumber(Mobilenumber);
                    ouser.setAddressline1(Addressline1);
                    ouser.setRole(Role);
                    //  ouser.setId(userid);
                    result.success(true);
                    result.setData(ouser);
//                result.setMessage(imgurl);
                    return result;
                }

            } catch (Exception e) {
                result.success(false);
                result.setData(null);
                result.message = "Error";
                return result;


            }

        }
        return null;
    }
}
