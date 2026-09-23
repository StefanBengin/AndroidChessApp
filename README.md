

## Setup

**Will not work after downloading.**

**After downloading:**
- create jniLibs folder inside the same directory as java folder
- create folders arm64-v8, armeabi-v7a, x86_64 inside jniLibs.
- compile stockfish binary 3 times with Android NDK:
  - make build ARCH=armv7 COMP=ndk EXTRALDFLAGS="-static-libstdc++" -j$(nproc)
  - make build ARCH=armv8 COMP=ndk EXTRALDFLAGS="-static-libstdc++" -j$(nproc)
  - make build ARCH=x86_64 COMP=ndk EXTRALDFLAGS="-static-libstdc++" -j$(nproc)  
- inside arm64-v8, armeabi-v7a and x86_64 insert stockfish binaries as libstockfish.so

Or instead of doing all those steps just make AppContainer.getOrCreateChessEngine() return FakeChessEngine, but that would make AI dumb.

<img width="600" height="882" alt="Screenshot (856)" src="https://github.com/user-attachments/assets/bce84bdc-3d3f-4125-99a9-1a29c138d8a3" />
