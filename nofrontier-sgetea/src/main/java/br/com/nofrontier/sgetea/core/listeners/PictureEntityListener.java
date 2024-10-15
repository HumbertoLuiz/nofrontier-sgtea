package br.com.nofrontier.sgetea.core.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.nofrontier.sgetea.core.models.Picture;
import br.com.nofrontier.sgetea.core.services.storage.adapters.StorageService;
import jakarta.persistence.PreRemove;

@Component
public class PictureEntityListener {

    @Autowired
    private static StorageService storageService;


    public void setStorageService(StorageService storageService) {
        PictureEntityListener.storageService = storageService;
    }

    @PreRemove
    private void preRemove(Picture picture) {
        storageService.remove(picture.getFilename());
    }

}
