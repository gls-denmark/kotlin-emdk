# Kotlin EMDK

## Product Vision
A wrapper library for the EMDK lib installed on Zebra devices
This library aims to provide the device serial obtained through EMDK

## Setup
To start using the app, the following needs to be added to your app

#### main build.gradle
You need to add the com.android.library plugin
    - id("com.android.library")

#### settings.gradle
The library requires you to have the EMDK-Android in your app, so you need to add the following repository
    - maven ("https://zebratech.jfrog.io/artifactory/EMDK-Android/")

#### app build.gradle
In the dependencies of your app you need to reference the implementation of the kemdk library
```
/* EMDK */
val emdkVersion = "0.0.1"
implementation("dk.gls:kemdk:$emdkVersion")
```

//TODO add setup of jitpack repository to actually download the lib


#### AndroidManifest.xml
For the library to work the following permissions and queries block needs to be added to your manifest:
```
<uses-permission android:name="com.symbol.emdk.permission.EMDK" />
<uses-permission android:name="com.zebra.provider.READ"/>

<!-- queries -->
<queries>
    <provider android:authorities="oem_info" />
    <package android:name="com.zebra.zebracontentprovider" />
    <package android:name="com.symbol.emdk.emdkservice" />
</queries>
```

And, inside the Application part of the manifest, you need to declare the usage of the the following:
```
<uses-library android:name="com.symbol.emdk"/>
```


#### EMDKConfig.xml
For the library to work, an EMDKConfig.xml file needs to be located in your app/main/assets folder.
It can be generated via the EMDK plugin for Android Studio, but it is probably easier to copy the below example and adjust it for your app
The following needs to be changed:
ProfileName
    - Change to your package name, but remember to leave the .debug (or other postfixes) for the different buildtypes
CallerPackageName
    - Needs to be the same as ProfileName
CallerSignature
    - The base64 encoding of your app signature for the given build variant
        - To get signature of app, you can use Zebras own Sigtools.jar and convert the result to base64
        - instructions found here: https://techdocs.zebra.com/emdk-for-android/latest/samples/sigtools/

Leave the rest as is

Example CallerSignature:
(This is just a random base64 string, but it is the approximated size of an app signature )
```
YWR2ZW50dXJlZmF0aGVycm9vdHBhc3R0cmFjZWVhY2hydWJiZXJoYWRldmVudGNoZWNrdGVhY2hlcmV4cGxhbmF0aW9ubmF0dXJlc3R1Y2toZWFkZWRvbmVjYW1wd2F0Y2hzaWx2ZXJzd2luZ293bmVyaW5zdHJ1bWVudHRydWNrd3JpdHRlbnNvbWV0aW1lYm9hcmRzYW5nbGVhdGhlcmNhbGxjaG9vc2VmaXJtc3BhY2ViZWNhbWV0ZW50b3JpZ2luYWxuZXh0ZmFtb3Vzd2lsbHRyb3BpY2Fsbm9uZXRoaXJ0eXBvbGl0aWNhbGRhcmtuZXNzdHJhZmZpY3NodXR3aGVuZXZlcmhpbXBsYW5ldGNvbG9ueW1vdG9yd2VsbGdhaW5yZXZpZXdmb29kYXNrdmFyaWV0eXdyaXRpbmdmb3VuZGJ1eXRlcm1ncmF2aXR5bWFwb250b3N0aWZmYXV0aG9yYWJpbGl0eWNvcm5ncmFiYmVkZW5lbXliaWxsY29hc3RzbWVsbHRlbXBlcmF0dXJlcG9zc2libHlwcm9ibGVtYm9hcmRjb25zdGFudGx5Y2hlc3RndWFyZGNpcmNsZXRydXRocG9ydGVsZXBoYW50b25seWlsbHRoZW5udW1lcmFscGllZXhhbXBsZXNwcmVhZGhpbGxtaW5lcmFsc2V4cGxhaW5sb29zZWF0bW9zcGhlcmVzbGlwcGVkcmVzcGVjdG5vb25ib3dhdHRhY2hlZGZlbGxvcHBvc2l0ZXNhd2NvbmRpdGlvbm5hbWViZWx0bW90b3Jib3ljaXJjdXNwcm90ZWN0aW9uc3VycHJpc2V0aHVzYmxvY2tjb2F0YmVzdHByb2M=
```


##### example xml:
```
<?xml version="1.0" encoding="UTF-8"?><!--This is an auto generated document. Changes to this document may cause incorrect behavior.-->
<wap-provisioningdoc>
    <characteristic type="ProfileInfo">
        <parm name="created_wizard_version" value="11.0.2" />
    </characteristic>
    <characteristic type="Profile">
        <parm name="ProfileName" value="com.example.debug" />
        <parm name="ModifiedDate" value="2023-06-09 11:28:22" />
        <parm name="TargetSystemVersion" value="11.4" />
        <characteristic type="AccessMgr" version="11.3">
            <parm name="CallerPackageName" value="com.example.debug" />
            <parm name="OperationMode" value="1" />
            <parm name="ServiceAccessAction" value="4" />
            <parm name="ServiceIdentifier"
                value="content://oem_info/oem.zebra.secure/build_serial" />
            <parm name="CallerSignature"
                value="Debug App Signature in base64" />
        </characteristic>
    </characteristic>
    <characteristic type="Profile">
        <parm name="ProfileName" value="com.example.debug.test" />
        <parm name="ModifiedDate" value="2023-06-09 11:28:22" />
        <parm name="TargetSystemVersion" value="11.4" />
        <characteristic type="AccessMgr" version="11.3">
            <parm name="CallerPackageName" value="com.example.debug.test" />
            <parm name="OperationMode" value="1" />
            <parm name="ServiceAccessAction" value="4" />
            <parm name="ServiceIdentifier"
                value="content://oem_info/oem.zebra.secure/build_serial" />
            <parm name="CallerSignature"
                value="Debug App Signature in base64" />
        </characteristic>
    </characteristic>
    <characteristic type="Profile">
        <parm name="ProfileName" value="com.example"/>
        <parm name="ModifiedDate" value="2023-06-09 11:28:22"/>
        <parm name="TargetSystemVersion" value="11.4"/>
        <characteristic type="AccessMgr" version="11.3">
            <parm name="CallerPackageName" value="com.example"/>
            <parm name="OperationMode" value="1"/>
            <parm name="ServiceAccessAction" value="4"/>
            <parm name="ServiceIdentifier" value="content://oem_info/oem.zebra.secure/build_serial"/>
            <parm name="CallerSignature" value="Production App Signature in base64" />
        </characteristic>
    </characteristic>
</wap-provisioningdoc>
```


## Typical errors
- **statusCode error: FAILURE Profile name not found in current config.**

    This is due to wrong EMDKConfig
    Make sure ProfileName and CallerPackageName both matches your package name.
    Some buildtypes, e.g. debug, often postfixes with .debug.
    Unittests postfixes with .test in addition so for tests, the config should have a your.package.name.debug.test
    You can find packagename via context.packageName

- **Error: This app does not have access to call OEM service.**

    There are several cases where this error can occur, see below
  
  - *statusCode CHECK_XML*
  
    Due to CallerSignature not matching signature of app.
    To get signature of app, you can use Zebras own Sigtools.jar and convert the result to base64 instructions found here: https://techdocs.zebra.com/emdk-for-android/latest/samples/sigtools/

  - *Failed to find provider info for oem_info*
  
    Missing queries in manifest - See setup for details
  
  - *other cause*
  
    Missing permission <uses-permission android:name="com.symbol.emdk.permission.EMDK" /> in manifest

- **java.lang.SecurityException: Permission Denial: opening provider com.zebra.dataprovider.provider.DataLakeProvider**

    Missing permission <uses-permission android:name="com.zebra.provider.READ"/> in manifest

- **java.lang.NoClassDefFoundError: Failed resolution of: Lcom/symbol/emdk/EMDKManager$EMDKListener;**

    Missing use lib in manifest - See setup for details