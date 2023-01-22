# FamKidMem (Family Moments)
'Selbstgedrehte Videos'

## Backend
This repository contains the sources for the FamKidMem Web-backend.


## Features
* **User-Management**
  * **Login/Logout**  (/api/user)
  * **Maintain Users** (/ccms/admin)
* **Add, update and delete Videos** (/cms/edit/video/ | encrypted)
* **Get Video Index, Thumbnails, m3u8 files and ts files** (/api/video and /api/ts | encrypted)

## Documentation
**/swagger-ui.html**

## Build
**mvn clean package**

## Run
**java -jar famkidmem-web-backend...jar [--files-dir <path-to-files>]**\
\# where <path-to-files> is: Path to directory where the files (thumbnails, m3u8, ts)
should be stored (default: ./files/).

## ApiKey
You will need a file named **ccms_auth_token_hash** in <path-to-files>.\
This file has to contain a bcrypt hash of desired auth_token for /ccms/... paths.\
This is needed to authorize the ccms application.\
The ccms application has to send the auth_token in header **CCMS-AUTH-TOKEN**

## Credits
* Spring boot

## Version
2.0

## Changelog (since 2.0)
### Version 2.0 (2023-01-08)
* New Feature: Comments
* New Feature: Chat
