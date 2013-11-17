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
import org.apache.log4j.Logger;

public class AdAutoClick {
    private final String pwuser;
    private final String pwpwd;
    private CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final String SITE = "http://9gal.com/";
    private static final String INDEX = SITE + "index.php";
    private static final String LOGIN = SITE + "login.php";
    private static final String BOXLINK = SITE + "kf_smbox.php";
    private static Logger logger = Logger.getLogger(AdAutoClick.class);

    public String post(String url, List<NameValuePair> params) {
        HttpPost hp = new HttpPost(LOGIN);

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
        return result;
    }

    private void login() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.addAll(Arrays.asList(new NameValuePair[] {
                new BasicNameValuePair("pwuser", pwuser),
                new BasicNameValuePair("pwpwd", pwpwd),
                new BasicNameValuePair("hideid", "0"),
                new BasicNameValuePair("cktime", "0"),
                new BasicNameValuePair("jumpurl", INDEX),
                new BasicNameValuePair("step", "2"),
                new BasicNameValuePair("lgt", "1") }));
        String result = post(LOGIN, params);
        // 登陆成功
        if (result.contains("您已经顺利登录") || result.contains("重复")) {
            logger.info("登陆成功");
        } else {
            logger.error("用户名密码错误");
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

    public String get(String link) {
        HttpGet httpget = new HttpGet(link);
        HttpResponse response;
        String result = null;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            logger.error("连接出现问题");
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
            min = 5 * 60;
            if (output.length() == 0) {
                output = findString(boxResult, "(获得了\\d+KFB的奖励)");
                min = 5 * 60;
            }
            logger.info(output);
        } else {
            min = 20;
            logger.info("您已点过");
        }
        logger.info("等待" + min + "分钟再获取");
        try {
            Thread.sleep(min * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 每隔6分钟刷新一次, 增加在线时间
    public void refresh() {
        new Thread() {
            @Override
            public void run() {
                for (;;) {
                    get(INDEX);
                    logger.info("刷新");
                    try {
                        Thread.sleep(6 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public AdAutoClick(String pwuser, String pwpwd) {
        this.pwuser = pwuser;
        this.pwpwd = pwpwd;

    }

    public void start() {
        this.refresh();
        this.autoDonate();
        for (int i = 1;; i++) {
            try {
                logger.info("第" + i + "次");
                this.clickAdAndGetKFB();
                this.levelUp();
            } catch (Exception e) {
                logger.error("发生错误, 20分钟后再次尝试");
                try {
                    Thread.sleep(20 * 60 * 60 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void start(int time) {
        for (int i = 1; i < time; i++) {
            try {
                logger.info("第" + i + "次");
                this.clickAdAndGetKFB();
                this.levelUp();
            } catch (Exception e) {
                logger.error("发生错误, 20分钟后再次尝试");
                try {
                    Thread.sleep(20 * 60 * 60 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static final String SMUP = SITE + "kf_fw_rvrc.php";

    @SuppressWarnings("unused")
    private int getSMLevel() {
        String result = get(SMUP);
        String smLevelStr = this.findString(result, "我的\"神秘\"等级为：([0-9]+)");
        return Integer.parseInt(smLevelStr);
    }

    private int getLevelUpKFB() {
        String result = get(SMUP);
        String levelUpKFB = this.findString(result, "升级需要消耗\"KFB\"： ([0-9]+)");
        return Integer.parseInt(levelUpKFB);
    }

    @SuppressWarnings("unused")
    private int getLevelUpKFB(int sMLevel) {
        if (sMLevel < 0) {
            return 100;
        } else if (sMLevel > 0 && sMLevel <= 50) {
            return (sMLevel + 2) * 100;
        } else if (sMLevel > 50 && sMLevel <= 200) {
            return 5000;
        } else {
            return 5000 + sMLevel * 3;
        }
    }

    private void autoDonate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                donate();
                try {
                    Thread.sleep(1000 * 60 * 60 * 24);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ;
    }

    private final static String DONATE = SITE + "kf_growup.php?ok=1";

    private void donate() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kfb", "1"));
        post(DONATE, params);
    }

    private int getNowKFB() {
        String result = get(SMUP);
        String nowKFB = this.findString(result, "我的\"KFB\"为：([0-9]+)");
        return Integer.parseInt(nowKFB);
    }

    private boolean levelUp() {
        if (getNowKFB() >= getLevelUpKFB()) {
            levelUpPost();
            return true;
        } else {
            return false;
        }
    }

    private final static String LVUP = SITE + "kf_fw_rvrc.php?rvrc=1";

    private void levelUpPost() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kf_fw_rvrc_tongyi", "1"));
        post(LVUP, params);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            logger.error("参数错误");
            System.exit(1);
        }
        String pwuser = args[0];
        String pwpwd = args[1];
        AdAutoClick aac = new AdAutoClick(pwuser, pwpwd);
        aac.start();
    }
}