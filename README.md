# MapView
Composable UI component for Desktop, Android and Browser

## Setup
In local.properties, add keys:
```
mapTilerSecretKey=xXxXxXxXxXxXx
sdk.dir=Path to Android SDK
```
Where **mapTilerSecretKey** is secret API key for cloud.maptiler.com  
Here you can get this key: https://cloud.maptiler.com/maps/streets/ (register and look at url field `?key=...#`)

## Usage
```Kotlin
MapView(
    modifier = Modifier.fillMaxSize(),
    mapTilerSecretKey = MAPTILER_SECRET_KEY,
    latitude =  60.00,
    longitude = 29.75,
    startScale = 840.0,
    onMapViewClick = { latitude, longitude ->
        println("Hello, Geo coordinates, (lat $latitude, lon $longitude)")
        true
    }
)
```

### Running desktop application
```
./gradlew :desktop:run
```

### Install Android application

```
./gradlew android:installDebug
```

Open project in IntelliJ IDEA or Android Studio and run "android" configuration.

### JS
```
./gradlew browser:jsBrowserRun
```
