package org.javatoy.consumer;


import org.javatoy.commons.UserTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  如果使用 RestTemplate 的话， 将不再使用 HttpURLConnection
 */
@RestController
public class UserHelloController {

    @Autowired
    @Qualifier( "restTemplateOne")
    RestTemplate restTemplateOne;

    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping("/hello1")
    public String hello1(){
        HttpURLConnection con =null;
        try {
            URL url =new URL("http://localhost:1113/hello");
            con = (HttpURLConnection) url.openConnection();
            if(con.getResponseCode() ==200){
                BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
                String s = br.readLine();
                br.close();
                return s;
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return "error";
    }

    @GetMapping("/hello2")
    public String hello2(){
        // 调用的微服务的名称
        List<ServiceInstance> list = discoveryClient.getInstances( "provider" );
        ServiceInstance instance = list.get( 0 );
        String host = instance.getHost();
        int port =instance.getPort();
        //  组装一下 微服务的信息
        StringBuffer sb  = new StringBuffer(  );
        sb.append( "http://" )
        .append( host )
        .append( ":" )
        .append( port )
        .append( "/hello" );

        /**
         * 使用 RestTemplate 一行代码进行http调用
         */
        String s = restTemplateOne.getForObject( sb.toString(), String.class );
        return s;
//        /**
//         * 使用 RestTemplate 进行代替
//         */
//        HttpURLConnection con =null;
//        try {
//           // URL url =new URL("http://localhost:1113/hello");
//            URL url =new URL(sb.toString());
//            con = (HttpURLConnection) url.openConnection();
//            if(con.getResponseCode() ==200){
//                BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
//                String s = br.readLine();
//                br.close();
//                return s;
//            }
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        return "error";
    }

    @Autowired
    @Qualifier( "restTemplate")
    RestTemplate restTemplate;

    //  线性负载均衡
    //int count =0;
    @GetMapping("/hello3")
    public String hello3(){
        //使用 RestTemplate 的 @LoadBalanced 来进行负载均衡
        return restTemplate.getForObject( "http://provider/hello",String.class );
//        // 调用的微服务的名称
//        List<ServiceInstance> list = discoveryClient.getInstances( "provider" );
//        ServiceInstance instance = list.get( (count++)%list.size() );
//        String host = instance.getHost();
//        int port =instance.getPort();
//        //  组装一下 微服务的信息
//        StringBuffer sb  = new StringBuffer(  );
//        sb.append( "http://" )
//                .append( host )
//                .append( ":" )
//                .append( port )
//                .append( "/hello" );
//
//        HttpURLConnection con =null;
//        try {
//            // URL url =new URL("http://localhost:1113/hello");
//            URL url =new URL(sb.toString());
//            con = (HttpURLConnection) url.openConnection();
//            if(con.getResponseCode() ==200){
//                BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
//                String s = br.readLine();
//                br.close();
//                return s;
//            }
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        return "error";
    }

    // RestTemplate的方法
    @GetMapping("hello4")
    public void hello4(){
        //getForObject 直接拿到了服务端的返回值
        String javatoy = restTemplate.getForObject( "http://provider/hello2?name={1}", String.class, "javatoy" );
        // getForEntity 不仅仅拿到返回值，还拿到请求头，状态码
        ResponseEntity<String> responseEntity = restTemplate.getForEntity( "http://provider/hello2?name={1}", String.class, "javatoy" );
        // 请求体
        String toy =responseEntity.getBody();
        // 请求状态码
        HttpStatus statusCode = responseEntity.getStatusCode();
        int statusCodeValue=responseEntity.getStatusCodeValue();
        // 响应头
        HttpHeaders headers =responseEntity.getHeaders();
        Set<String > keySet = headers.keySet();
        for (String s:keySet){
            System.out.println(s);
        }
    }

    // 传参方式
    @GetMapping("/hello5")
    public void hello5() throws UnsupportedEncodingException {
        // 方法1  直接传参
        String s1 =restTemplate.getForObject( "Http://provider/hello2?name={1}",String.class,"javatoy" );
        // 方法2 构建Map 传参
        Map<String ,Object> map = new HashMap<>(  );
        map.put( "name","zhangsan" );
        s1 = restTemplate.getForObject( "Http://provider/hello2?name={name}",String.class,map );
        // 转码传参
        String url = "Http://provider/hello2?name="+ URLEncoder.encode("", "UTF-8");
        URI uri = URI.create(url);
        s1 =restTemplate.getForObject( uri,String.class );
    }


    @PostMapping("/hello6")
    public void hello6(){
        // post中， 传递的第二个参数是 MultiValueMap 的话，则参数是以 Key/value 形式传递的
        MultiValueMap<String,Object> map =new LinkedMultiValueMap<>(  );
        map.add( "id","99" );
        map.add( "username","javatoy" );
        map.add( "password","123456" );
        UserTest userTest = restTemplate.postForObject( "Http://provider/user1", map, UserTest.class );

        //  如果  第二个参数是 普通参数， 则是以 json 形式传递的
        userTest.setId( 98 );
        userTest = restTemplate.postForObject( "Http://provider/user2",userTest,UserTest.class );
    }

    // 注册-重定向到登录页 使用 postForLocation
    @PostMapping("/hello7")
    public void hello7(){
        MultiValueMap<String ,Object> map = new LinkedMultiValueMap<> ();
        map.add( "id","88" );
        map.add( "username","lisi" );
        map.add( "password","123456" );
        //  获取重定向的地址
        URI uri = restTemplate.postForLocation( "Http://provider/register", map, UserTest.class );
        // 拿到uri之后发送新的请求
        restTemplate.getForObject( uri,String.class );
    }

    @PostMapping("/hello8")
    public void hello8(){
        MultiValueMap<String ,Object> map = new LinkedMultiValueMap<> ();
        map.add( "id","88" );
        map.add( "username","lisi" );
        map.add( "password","123456" );
        restTemplate.put( "Http://provider/user3", map );

        UserTest userTest = new UserTest();
        userTest.setId( 89 );
        userTest.setUsername( "zhangsan" );
        userTest.setPassword("lisi");
        restTemplate.put( "Http://provider/user4",userTest);
    }

    @GetMapping("/hello9")
    public void hello9(){
        restTemplate.delete( "Http://provider/user5?ud={1}",99 );
        restTemplate.delete( "Http://provider/user6/{1}",99 );
    }
}
