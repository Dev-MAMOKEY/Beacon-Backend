//백그라운드 알림 수신용 Service Worker
importScripts('https://www.gstatic.com/firebasejs/10.12.2/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.2/firebase-messaging-compat.js');

//▼ fcm-test.html과 동일한 값 ▼
firebase.initializeApp({
    apiKey: "AIzaSyByg69lVNuzmS14SWb_9GhX-o9wme-m6m8",
    authDomain: "beacom-98b44.firebaseapp.com",
    projectId: "beacom-98b44",
    storageBucket: "beacom-98b44.firebasestorage.app",
    messagingSenderId: "484018299868",
    appId: "1:484018299868:web:0155126f51d33ea78aa519"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log('[SW] 백그라운드 메시지:', payload);
    const n = payload.notification || {};
    self.registration.showNotification(n.title || '알림', {
        body: n.body || '',
        icon: '/favicon.ico'
    });
});
