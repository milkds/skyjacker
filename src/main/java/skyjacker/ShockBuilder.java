package skyjacker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import skyjacker.entities.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShockBuilder {
    private static final Logger logger = LogManager.getLogger(ShockBuilder.class.getName());

    public SkyShock buildShock(WebDriver driver, String link) {
        SkyShock shock = new SkyShock();
        shock.setWebLink(link);
        try {
            SileniumUtil.openLink(driver, link);
        }
        catch (TimeoutException e){
            logger.error("Couldn't get shock page " + link);
            return shock;
        }
        setShockInfo(shock, driver);
        logger.info("Built shock: " + shock);
        return shock;
    }

    private void setShockInfo(SkyShock shock, WebDriver driver) {
        String title = getTitle(driver);
        String sku = getSKU(driver);
        String desc = getDescription(driver);
        String imgLink = getImgLink(driver);
        Set<Category> categories = getCategories(driver);
        Set<SpecAndKitNote> notes = getSpecAndKitNotes(driver);
        Set<Fitment> fitments = getFitments(driver);

        fitments.forEach(fitment -> {
            processFitNotes(shock, fitment);
            fitment.setSkyShock(shock);
        });

        shock.setTitle(title);
        shock.setSku(sku);
        shock.setDesc(desc);
        shock.setImgLinks(imgLink);
        shock.setCategories(categories);
        shock.setFitments(fitments);
        shock.setNotes(notes);
    }

    private void processFitNotes(SkyShock shock, Fitment fitment) {
        Set<FitmentNote> rawFitNotes = fitment.getFitNotes();
        Set<FitmentNote> finalFitNotes = new HashSet<>();
        Set<SpecAndKitNote> shockNotes = shock.getNotes();
        Set<String> fitNoteExceptions = getFitNoteExceptions();
        rawFitNotes.forEach(fitNote->{
            String note = fitNote.getFitNote();
            if (note.contains("Extended")||note.contains("Collapsed")){
                SpecAndKitNote lengthNote = getLengthNote(note);
                if (lengthNote!=null){
                    shockNotes.add(lengthNote);
                }
            }
            else if (note.startsWith("Front")||note.startsWith("Rear")){
                if (!note.endsWith("Lift")){
                    note = normalizeLiftNote(note);
                }
                if (note.length()>10){
                    fitNote.setFitNote(note);
                    finalFitNotes.add(fitNote);
                }
            }
            else if(fitNoteExceptions.contains(fitNote.getFitNote())){
                finalFitNotes.add(fitNote);
            }
            else if (note.length()>5){
                SpecAndKitNote shNote = new SpecAndKitNote();
                shNote.setName("Note");
                shNote.setValue(note);
                shockNotes.add(shNote);
            }
        });
        fitment.setFitNotes(finalFitNotes);
    }

    private Set<String> getFitNoteExceptions() {
        Set<String> result = new HashSet<>();
        result.add("0-1 in. Lift");
        result.add("0-2 in. Lift");
        result.add("1-2.5 in. Lift");
        result.add("1-2.5 inches of lift");
        result.add("2WD");
        result.add("4WD");
        result.add("RWD");

        return result;
    }

    private String normalizeLiftNote(String note) {
        //todo: implement
        return note;
    }

    private SpecAndKitNote getLengthNote(String note) {
        String lengthType = "";
        if (note.contains("Collapsed")){
            lengthType = "Collapsed Length";
            note = note.replace("Collapsed", "");
        }
        else {
            lengthType = "Extended Length";
            note = note.replace("Extended", "");
        }
        note = note.trim();
        if (note.length()==0){
            return null;
        }
        if (note.contains("in.")){
            note = note.replace("in.", "");
            note = note.trim();
        }
        //checking if length has legal value
        try {
            Double d = Double.parseDouble(note);
        }
        catch (NumberFormatException e){
            return null;
        }
        SpecAndKitNote skNote = new SpecAndKitNote();
        skNote.setName(lengthType);
        skNote.setValue(note);

        return skNote;
    }

    private Set<Fitment> getFitments(WebDriver driver) {
        Set<Fitment> fitments = new HashSet<>();
        WebElement fitTab = SileniumUtil.getElementBy(driver, By.id("tab-title-gamma_ymm_tab"));
        if (fitTab==null){
            logger.error("No fitment tab at " + driver.getCurrentUrl());
            return fitments;
        }
        fitTab.click();
        SileniumUtil.getElementBy(driver, By.className("gamma_ymm_term_info"));//to wait for tab to open
        List<WebElement> fitElements = driver.findElements(By.className("gamma_ymm_term_info"));
        if (fitElements.size()==0){
            logger.error("No info in fitment tab for: " + driver.getCurrentUrl());
            return fitments;
        }
        fitElements.forEach(fitEl->{
            Fitment fitment = new Fitment();
            WebElement fitStringEl = SileniumUtil.getElementFromElementBy(fitEl, By.tagName("h4"));
            if (fitStringEl==null){
                fitment.setFitString("NO FITMENT STRING");
            }
            else {
                fitment.setFitString(fitStringEl.getText());
                logger.debug("FIT STRING: " + fitment.getFitString());
            }
            Set<FitmentNote> fitNotes = fitment.getFitNotes();
            List<WebElement> notesEls = fitEl.findElements(By.tagName("li"));
            logger.debug("FITMENT NOTES: ");
            Set<String> duplicateRemoveSet = new HashSet<>();
            notesEls.forEach(notesEl->{
                String fitNote = notesEl.getText();
                FitmentNote note = new FitmentNote();
                note.setFitNote(fitNote);
                fitNotes.add(note);
                logger.debug(note);
            });
            fitments.add(fitment);
        });

        return fitments;
    }

    private Set<SpecAndKitNote> getSpecAndKitNotes(WebDriver driver) {
        Set<SpecAndKitNote> notes = new HashSet<>();
        WebElement notesTab = SileniumUtil.getElementBy(driver, By.id("tab-title-additional_information"));
        if (notesTab == null){
            logger.error("No Spec and Kit Notes tab for " + driver.getCurrentUrl());
            return notes;
        }
        notesTab.click();
        WebElement notesGroup = SileniumUtil.getElementBy(driver, By.className("shop_attributes"));
        if (notesGroup==null){
            logger.error("No data in Spec and Kit Notes tab for " + driver.getCurrentUrl());
            return notes;
        }
        List<WebElement> tableRowEls = notesGroup.findElements(By.tagName("tr"));
        logger.debug("NOTES: ");
        tableRowEls.forEach(tableRowEl->{
            String name = tableRowEl.findElement(By.tagName("th")).getText();
            String value = tableRowEl.findElement(By.tagName("td")).getText();
            SpecAndKitNote note = new SpecAndKitNote();
            note.setName(name);
            note.setValue(value);
            notes.add(note);
            logger.debug(note);
        });

        return notes;
    }

    private String getImgLink(WebDriver driver) {
        String imgLink = "NO IMG LINK";
        WebElement imgElGroup = SileniumUtil.getElementBy(driver, By.className("woocommerce-product-gallery__image"));
        if (imgElGroup==null){
            return imgLink;
        }
        List<WebElement> imgEls = imgElGroup.findElements(By.tagName("img"));
        if (imgEls.size()==0){
            return imgLink;
        }
        StringBuilder imgLinkBuilder = new StringBuilder();
        imgEls.forEach(imgEL->{
            imgLinkBuilder.append(imgEL.getAttribute("src"));
            imgLinkBuilder.append(System.lineSeparator());
        });
        int length = imgLinkBuilder.length();
        if (length>0){
            imgLinkBuilder.setLength(length-2);
            logger.debug("IMG LINKS: " + imgLinkBuilder.toString());
            return imgLinkBuilder.toString();
        }
        else {
            return imgLink;
        }
    }

    private String getDescription(WebDriver driver) {
        WebElement descEl = SileniumUtil.getElementBy(driver, By.id("tab-description"));
        if (descEl==null){
            return "NO DESCRIPTION";
        }
        String desc = descEl.getText();
        desc = desc.replace("Description", "");
        if (desc.length()>2){
            desc = desc.substring(1);
        }
        else {
            desc = "NO DESCRIPTION";
        }
        logger.debug("DESCRIPTION: " + desc);

        return desc;
    }

    private Set<Category> getCategories(WebDriver driver) {
        Set<Category> categories = new HashSet<>();
        WebElement catElGroup = SileniumUtil.getElementBy(driver, By.className("posted_in"));
        List<WebElement> catEls = catElGroup.findElements(By.tagName("a"));
        catEls.forEach(catEl-> {
            Category category = new Category();
            category.setName(catEl.getText());
            categories.add(category);
        });
        logger.debug("Categories: ");
        categories.forEach(logger::debug);

        return categories;
    }

    private String getSKU(WebDriver driver) {
        WebElement skuEl = SileniumUtil.getElementBy(driver, By.className("sku"));
        if (skuEl==null){
            return "NO SKU";
        }
        logger.debug("SKU: " + skuEl.getText());

        return skuEl.getText();
    }

    private String getTitle(WebDriver driver) {
        WebElement titleEl = SileniumUtil.getElementBy(driver, By.cssSelector("h1[class='product_title entry-title']"));
        if (titleEl==null){
            return "NO TITLE";
        }
        logger.debug("Title: " + titleEl.getText());

        return titleEl.getText();
    }

}
