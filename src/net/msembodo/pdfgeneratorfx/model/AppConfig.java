package net.msembodo.pdfgeneratorfx.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AppConfig {

    private String pdfGeneratorJar;
    private String template;
    private String dataCsv;
    private String outputFolderPath;

    public AppConfig() {}

    public AppConfig(String pdfGeneratorJar, String template, String dataCsv, String outputFolderPath) {
        this.pdfGeneratorJar = pdfGeneratorJar;
        this.template = template;
        this.dataCsv = dataCsv;
        this.outputFolderPath = outputFolderPath;
    }

    public String getPdfGeneratorJar() {
        return pdfGeneratorJar;
    }

    @XmlElement
    public void setPdfGeneratorJar(String pdfGeneratorJar) {
        this.pdfGeneratorJar = pdfGeneratorJar;
    }

    public String getTemplate() {
        return template;
    }

    @XmlElement
    public void setTemplate(String template) {
        this.template = template;
    }

    public String getDataCsv() {
        return dataCsv;
    }

    @XmlElement
    public void setDataCsv(String dataCsv) {
        this.dataCsv = dataCsv;
    }

    public String getOutputFolderPath() {
        return outputFolderPath;
    }

    @XmlElement
    public void setOutputFolderPath(String outputFolderPath) {
        this.outputFolderPath = outputFolderPath;
    }

}
