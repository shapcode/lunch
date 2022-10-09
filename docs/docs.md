## General Architecture
The application follows a general MVVM + Repository pattern. For simplicity sake, the repository
uses a simple memory cache, rather than a more robust persistent store. For a more complex app
one would likely choose a more robust caching mechanism such as Room. When data doesn't exist in
the cache, a call is made to the api then stored in the cache. The ViewModel handles making
calls to the repository and delivering the data as flows.

## Technologies Used
* Language:
  * Kotlin
* Libraries:
  * Jetpack
    * Navigation
    * Lifecycle
    * ConstraintLayout
    * RecyclerView
    * CardView
    * Compose
    * ViewBinding
  * Material Components for Android
  * Hilt for dependency injection
  * Retrofit for API calls
  * Moshi for parsing json
  * Coil for image loading
  * Timber for logging
* APIs:
  * Google Place

## Running
In local.properties, add the following keys. The secrets gradle plugin will handle making them
available to the BuildConfig and AndroidManifest. 

```
MAPS_API_KEY=<Your Google Maps API Key>
PLACE_API_KEY=<Your Google Place API Key>
```