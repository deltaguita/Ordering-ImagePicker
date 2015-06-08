# Ordering-ImagePicker
An ordering multi-select gallery


## Usage



code:

    Intent intent = new Intent(this, OrderingGalleryActivity.class);
    startActivityForResult(intent, REQUEST_CODE_PHOTOS);

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHOTOS)
        handlePhotos(resultCode,data);
    }

    private void handlePhotos(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        String[] all_path = data.getStringArrayExtra("all_path");
    }