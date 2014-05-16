UiTagenda
=========

Hoe werkende krijgen?

1. Download de zip en pak deze uit
2. Open het project in Android Studio
3. Klik op "Sync Project with Gradle Files"
4. Open strings.xml en pas de volgende zaken aan: consumer_key en consumer_secret
5. Open analytics.xml en pas het volgende aan: ga_trackingId
6. Open MainActivity.java en vul de volgende token in: NewRelic.withApplicationToken("").start(this.getApplication());
7. Klaar om te runnen!