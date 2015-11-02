package com.muno.photoalbum.ImagesManagement;

/**
 * Created by InX on 26/10/2015.
 */
public class PhotosSelected {
    boolean[] selected;
    int size;

    public PhotosSelected(boolean[] sel, int s) {
        this.size = s;
        setNewPageSelected(sel);
    }

    public void setNewPageSelected(boolean[] sel) {
        this.selected = sel;
    }
    public boolean[] getPageSelected() {
        return selected;
    }
    public boolean getSingleSelected(int i){
        return  selected[i];
    }
    public int getObjectSize() { return size;}
}
