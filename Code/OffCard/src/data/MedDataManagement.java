package data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class MedDataManagement {
	private static MedDataManagement reference;

	private JAXBContext context;
	private MedData data;

	public static void main(String[] args) {
		MedData tempData = new MedData();
		MedEntry temp = new MedEntry();
		temp.setId(0);
		temp.setName("unknown");
		tempData.data.add(temp);

		File file = new File("meddata.xml");
		if (!file.getAbsoluteFile().exists())
			try {
				file.createNewFile();
				System.out.println("File existiert: " + file.exists());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			JAXBContext context = JAXBContext.newInstance(MedData.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(tempData, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MedDataManagement() {
		try {
			this.context = JAXBContext.newInstance(MedData.class);
			this.data = (MedData) (this.context.createUnmarshaller()).unmarshal(new File("meddata.xml"));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static MedDataManagement getInstance() {
		if (reference == null)
			reference = new MedDataManagement();
		return reference;
	}

	public List<MedEntry> search(String searchString) {
		return this.data.search(searchString);
	}
	
	public String getMedName(int medId){
		return data.getMedName(medId);
	}
}
