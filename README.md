UiTagenda
=========

Hoe werkende krijgen?

1. Download de zip en pak deze uit
2. Open het project in Android Studio
3. Open 'app/fabric.properties' en vul de Fabric api secret in
4. Open 'app/src/main/AndroidManifest.xml' en vul de Fabric api key in
5. Voeg de release ('my_release_key.jks') en debug ('debug.keystore') keystores toe aan de map 'keystores'
6. Open 'app/build.gradle' en vul de keystore wachtwoorden in
7. Open 'app/src/main/java/org/uitagenda/api/ApiHelper.java' en vul de OAuth consumer key en secret in
8. Open 'app/src/main/res/xml/app_tracker.xml' en vul het Google Analytics tracking ID in
9. Klik op 'Sync Project with Gradle Files'
10. Klaar om te runnen!