package com.waither.config;//package com.waither.notiservice.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Configuration
//@RequiredArgsConstructor
//public class FirebaseConfig {
//
//    @Value("${firebase.key.path}")
//    private String keyPath;
//
//    @PostConstruct
//    public void initializeApp() throws IOException {
//        FileInputStream serviceAccount =
//                new FileInputStream(keyPath);
//
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        FirebaseApp.initializeApp(options);
//    }
//}
