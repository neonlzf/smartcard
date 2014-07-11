package data;

import gui.MedEdit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class MedData {
	@XmlElement
	public ArrayList<MedEntry> data;

	public MedData() {
		data = new ArrayList<MedEntry>();
	}

	public List<MedEntry> search(String toSearch) {
		ArrayList<MedEntry> ret = new ArrayList<MedEntry>();
		String searchString = toSearch.trim().toLowerCase();
		for (MedEntry me : this.data) {
			if (me.getName().toLowerCase().contains(searchString))
				ret.add(me);
		}
		return ret;
	}

	public String getMedName(int medId) {
		for (MedEntry me : this.data) {
			if (me.getId() == medId)
				return me.getName();
		}
		return "unknown";
	}
}
