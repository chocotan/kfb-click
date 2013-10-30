package io.loli.kf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class AdAutoClick {
    private final String pwuser;
    private final String pwpwd;
    private static CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final String SITE = "http://9gal.com/";
    private static final String INDEX = SITE + "index.php";
    private static final String LOGIN = SITE + "login.php";
    private static final String BOXLINK = SITE + "kf_smbox.php";

    public AdAutoClick(String pwuser, String pwpwd) {
        this.pwuser = pwuser;
        this.pwpwd = pwpwd;
    }

    private void login() {
        HttpPost hp = new HttpPost(LOGIN);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.addAll(Arrays.asList(new NameValuePair[] {
                new BasicNameValuePair("pwuser", pwuser),
                new BasicNameValuePair("pwpwd", pwpwd),
                new BasicNameValuePair("hideid", "0"),
                new BasicNameValuePair("cktime", "0"),
                new BasicNameValuePair("jumpurl", INDEX),
                new BasicNameValuePair("step", "2"),
                new BasicNameValuePair("lgt", "1") }));
        CloseableHttpResponse response = null;
        String result = null;
        try {
            hp.setEntity(new UrlEncodedFormEntity(params, "GBK"));
            response = httpclient.execute(hp);
            result = EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 登陆成功
        if (result.contains("您已经顺利登录") || result.contains("重复")) {
            System.out.println("登陆成功, 开始点广告");
        } else {
            System.err.println("用户名密码错误");
            System.exit(1);
        }
    }

    private String getAdLink() {
        String result = get(INDEX);
        String adlink = findString(result,
                "(diy_ad_move.php\\?[0-9a-zA-Z=&]+)\"");
        String finalLink = SITE + adlink;
        return finalLink;
    }

    private String get(String link) {
        HttpGet httpget = new HttpGet(link);
        HttpResponse response;
        String result = null;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String findString(String html, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(html);
        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }

    private String getKfbLink() {
        String result = get(BOXLINK);
        String adlink = findString(result, "(kf_smbox.php\\?[0-9a-zA-Z=&]+)\"");
        String finalLink = SITE + adlink;
        return finalLink;
    }

    public void clickAdAndGetKFB() {
        login();
        get(getAdLink());
        String boxResult = get(getKfbLink());
        // 获取两次, 防止点击之前已经被别人点击了
        String output = findString(boxResult, "(获得了\\d+KFB的奖励)");
        int min = 0;
        if (boxResult.contains("获得了")) {
            if (output.length() == 0) {
                output = findString(boxResult, "(获得了\\d+KFB的奖励)");
                min = 5 * 60;
            }
            System.out.println(output);
        } else {
            min = 20;
            System.out.print("您已点过, ");
        }
        System.out.println("等待" + min + "分钟再获取");
        try {
            Thread.sleep(min * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("参数错误");
            System.exit(1);
        }
        String pwuser = args[0];
        String pwpwd = args[1];

        for (int i = 1;; i++) {
            try {
                AdAutoClick aac = new AdAutoClick(pwuser, pwpwd);
                System.out.println("第" + i + "次");
                aac.clickAdAndGetKFB();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("发生错误, 20分钟后再次尝试");
                try {
                    Thread.sleep(20 * 60 * 60 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
