package uk.lug.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import uk.lug.serenity.npc.model.Person;

public class PersonUtils {


	public static byte[] encode(Person model) throws IOException {
		Element xml = model.getXML();
		XMLOutputter output = new XMLOutputter(org.jdom.output.Format
				.getPrettyFormat());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			output.output(xml, bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bos.close();
		return bos.toByteArray();
	}
	
	public static Person decode(byte[] data) throws JDOMException, IOException {
		ByteArrayInputStream instream = new ByteArrayInputStream(data);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(instream);
		Element root = doc.getRootElement();
		Person person = new Person();
		person.setXML(root);
		return person;
	}
}
