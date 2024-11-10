#!/usr/bin/env bash

set -eu

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
 SDK_VERSION="2.2.0"

 rm -rf "${SCRIPT_DIR}/../libs"
 mkdir -p "${SCRIPT_DIR}/../libs"

 pushd "${SCRIPT_DIR}/../libs"
 wget "https://switchboard-sdk-public.s3.amazonaws.com/builds/release/${SDK_VERSION}/android/SwitchboardSDK.aar"
 wget "https://switchboard-sdk-public.s3.amazonaws.com/builds/release/${SDK_VERSION}/android/SwitchboardVoicemod.aar"
 wget "https://switchboard-sdk-public.s3.amazonaws.com/builds/release/${SDK_VERSION}/android/SwitchboardUI.aar"
 wget "https://switchboard-sdk-public.s3.amazonaws.com/builds/release/${SDK_VERSION}/android/SwitchboardRNNoise.aar"
 popd


# VoiceData download and setup
VOICE_DATA_URL="https://switchboard-sdk-public.s3.amazonaws.com/assets/Voicemod/3.12.1/VoiceData.zip"
ASSETS_FOLDER="${SCRIPT_DIR}/../app/src/main/assets/"
VOICE_DATA_FOLDER="${ASSETS_FOLDER}/VoiceData"
ZIP_FILE="${SCRIPT_DIR}/VoiceData.zip"

# Remove previous VoiceData folder if it exists
if [ -d "$VOICE_DATA_FOLDER" ]; then
    echo "Removing previous $VOICE_DATA_FOLDER folder..."
    rm -rf "$VOICE_DATA_FOLDER"
fi

# Download VoiceData.zip to SCRIPT_DIR
echo "Downloading VoiceData.zip to $SCRIPT_DIR..."
curl -o "$ZIP_FILE" "$VOICE_DATA_URL"

# Unzip the downloaded file directly into ASSETS_FOLDER
echo "Unzipping $ZIP_FILE into $ASSETS_FOLDER..."
unzip "$ZIP_FILE" -d "$ASSETS_FOLDER"

# Remove the zip file after extraction
echo "Removing $ZIP_FILE..."
rm "$ZIP_FILE"

echo "Done!"
