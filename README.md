# MiVIP SDK v3.6.10 for Android


MiVIP’s Native SDK is a fully orchestrated user interface and user journey delivered as an SDK for seamless integration into any native application. The functionality is replicated from the existing Web journey where the same orchestration and white-label customisations are applied, as configured in one centralised location via the web portal. The SDK is packaged together with Mitek’s capture technology, MiSnap. Mitek’s customers can benefit from both Mitek’s market leading capture experience combined with a completely pre-built dynamic user journey, all delivered in a single packaged SDK with low code integration for minimum integration effort and accelerated time to live.


- - -

# System Requirements

<center>

| Technology | version |
| :--- | :---: |
| MiSnap | 5.8.1 |
| Android Gradle Plugin | 8.3.0 |
| Gradle | 8.4 |
| Kotlin | 1.8.10 |
| CameraX | 1.3.0 |
| JDK | 1.8 |
| Android min API level | 26 |
| Android target API level | 34 |

</center>

For MiSnap integration visit [MiSnap Android repository](https://github.com/Mitek-Systems/MiSnap-Android)

- - -

# Adding to your project

* Add [MiSanap SDKs](https://github.com/Mitek-Systems/MiSnap-Android) and obtain a license key
* Copy MiVIP SDKs to application libs folder
* In app build.gradle file add local file dependancy

```gradle
implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])
```
- - -

# SDK usage

* Start ID request activity with request QR code scan

```kotlin
btn_scan_qr.setOnClickListener {
    val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
        putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
        putExtra(MiVIPActivity.ACTION_FLAG, com.hooyu.android.HooyuActivity.ACTION_QR) // go to QR screen
        putExtra(MiVIPActivity.SOUNDS_DISABLED, true) // default is False
        putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // default is False
        putExtra(MiVIPActivity.DOCUMENT_CALLBACK_URL, docCallbackUrl) // if want to receive server callback at document processing
        putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false
    }
    mivipActivityResult.launch(intent)
}
```

* Open request with given MiVIP request ID

```kotlin
btn_request.setOnClickListener {
    val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
        putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
        putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_REQUEST) // open request
        val mivipRequestId = "8ec4dd13-ad90-4176-ba77-f57770af291d"
        putExtra(MiVIPActivity.MIVIP_REQUEST_ID, mivipRequestId) // ID request
        putExtra(MiVIPctivity.DOCUMENT_CALLBACK_URL, docCallbackUrl) // if want to receive server callback at document processing
        putExtra(MiVIPActivity.SOUNDS_DISABLED, false) // this is the default value (sounds on)
        putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // this is the default value (wallet off)
        putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false
    }
    mivipActivityResult.launch(intent)
}
```

* Get SDK results 

```kotlin
private val mivipActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    MiVIPActivity.Result.result?.let { res ->
        res.scoreResult?.let {
            Log.i("MIVIP", "Score result $it")
        }
        res.request?.let {
            Log.i("MIVIP", "Resuest $it")
        }
    }
    MiVIPActivity.Result.clearResult()
}
```

- - -

# SDKs Files and Sizes

* MiVIP-api-release.aar - includes API calls and handle results. Size - 4.2MB
* MiVIP-core-release.aar - implementation of active liveness and core functionality. Size - 254KB
* MiVIP-sdk-release.aar - includes journey orchestration and UI. Size - 8.4MB
* rp-sdk-1.02-release.aar - Samsung Wallet SDK for RP. Size - 41KB

- - -

# Integration Guide
* [MiVIPSdk](Docs/dev_guide_android.md)

- - -

# Third-Party Licenses

### [3-Clause BSD License](https://opensource.org/licenses/BSD-3-Clause)
* #### [OpenCV](https://github.com/opencv/opencv/blob/4.4.0/LICENSE)

### [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* #### [Apache Commons Imaging](https://github.com/apache/commons-imaging/blob/master/LICENSE.txt)
* #### [Kotlin](https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt)
* #### [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization/blob/master/license/)
* #### [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines/tree/master/license/)
* #### [Material Components for Android](https://github.com/material-components/material-components-android/blob/master/LICENSE)


- - - -

# Terms and Conditions

By you accessing this SDK and/or any related documentation, sample apps and libraries (collectively, the “SDK”) you acknowledge that either (i) you currently are a party to a  contract/license with Mitek Systems, Inc. (“Mitek”) which governs access to and use of the SDK (in which case, the terms and conditions of that contract govern your use of the SDK) or (ii) you agree to the following terms and conditions:

You will use the SDK solely to evaluate the SDK for the purpose of internal development of software and applications to the extent necessary for such applications to transfer data to Mitek.  You have no right to receive any underlying software or a copy of the object code or source code to the SDK.   You will not make copies of the SDK. You will not make the SDK available to any third party.   Mitek shall own all modifications, revisions, or derivative works of the code contained in its SDK.

Your right to use the SDK is conditional upon the following. You may not: (i) sell, rent, lease or otherwise distribute or share the SDK; (ii) disassemble, decompile, reverse engineer the SDK or output from the foregoing or otherwise attempt to derive the source code (or the underlying ideas, algorithms, structure or organization); (iii) provide access to the SDK to any third party; (iv) create any derivative works based upon the SDK;  (v) access the SDK in order to build a competitive solution or gather competitive intelligence or to assist someone else to build a competitive solution or gather competitive intelligence; (vi) use the SDK in a way that violates any applicable law; (vii) interfere with or disrupt the integrity or performance of the SDK, or (viii) attempt to gain unauthorized access to Mitek’s related systems or networks.

Mitek is giving permission to you to use the SDK solely in accordance with these terms and conditions.  The SDK is not being sold. Mitek and/or its licensors retain the exclusive ownership of all rights, title and interest, including all intellectual property rights, in and to the SDK(s) and associated documentation, including but not limited (i) any components, corrections, updates, upgrades, methodologies, derivative works and associated documentation thereof and (ii) all trademarks, trade names, trade secrets and other proprietary information of Mitek related thereto. Mitek also owns all suggestions, ideas, enhancement requests, or feedback related to the SDK.

All rights not expressly granted to you are reserved by Mitek and its licensors, and you shall have no rights which arise by implication or estoppel. 

- - - -


