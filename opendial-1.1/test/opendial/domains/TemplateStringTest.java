// =================================================================                                                                   
// Copyright (C) 2011-2015 Pierre Lison (plison@ifi.uio.no)
                                                                            
// Permission is hereby granted, free of charge, to any person 
// obtaining a copy of this software and associated documentation 
// files (the "Software"), to deal in the Software without restriction, 
// including without limitation the rights to use, copy, modify, merge, 
// publish, distribute, sublicense, and/or sell copies of the Software, 
// and to permit persons to whom the Software is furnished to do so, 
// subject to the following conditions:

// The above copyright notice and this permission notice shall be 
// included in all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// =================================================================                                                                   

package opendial.domains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.arch.Logger;
import opendial.datastructs.Assignment;
import opendial.datastructs.Template;
import opendial.readers.XMLDomainReader;
import opendial.utils.MathUtils;

import org.junit.Test;

/**
 * 
 *
 * @author  Pierre Lison (plison@ifi.uio.no)
 * @version $Date:: 2014-12-07 02:21:40 #$
 *
 */
public class TemplateStringTest {

	// logger
	public static Logger log = new Logger("TemplateStringTest",
			Logger.Level.DEBUG);
	
	

	@Test
	public void testTemplate1() {
		Template template = new Template("this is a first test");
		String utterance = "bla bla this is a first test bla";
		assertTrue(template.match(utterance, false).isMatching());
	}
	
	@Test
	public void testTemplate2() {
		Template template = new Template("hi my name is {name}");
		String utterance1 = "hi my name is Pierre, how are you?";
		assertTrue(template.match(utterance1, false).isMatching());
		String utterance2 = "hello how are you?";
		assertFalse(template.match(utterance2, false).isMatching());
		String utterance3 = "hi my name is Pierre";
		assertTrue(template.match(utterance3, false).isMatching());
		assertTrue(template.match(utterance3, true).isMatching());
	}
	
	@Test
	public void testTemplate3() {
		Template template = new Template("hi my name is {name} and I need coffee");
		String utterance1 = " hi my name is Pierre and i need coffee ";
		String utterance2 = "hi my name is Pierre and I need coffee right now";
		assertTrue(template.match(utterance1, false).isMatching());
		assertTrue(template.match(utterance2, false).isMatching());
		String utterance3 = "hello how are you?";
		assertFalse(template.match(utterance3, false).isMatching());
		
		assertFalse(template.match(utterance3, true).isMatching());
		assertTrue(template.match(utterance1, true).isMatching());
	}
	 
	@Test
	public void testTemplate4() {
		Template template1 = new Template("hi my name is {name}");
		assertEquals("Pierre Lison", template1.match("hi my name is Pierre Lison ", true).
				getFilledSlots().getValue("name").toString());

		Template template2 = new Template("{name} is my name");
		assertEquals("Pierre Lison", template2.match("Pierre Lison is my name", true).
				getFilledSlots().getValue("name").toString());

		
		Template template3 = new Template("hi my name is {name} and I need coffee");
		assertEquals("Pierre", template3.match("hi my name is Pierre and I need coffee ", true).
				getFilledSlots().getValue("name").toString());
	}
	
	@Test
	public void testTemplate5() {
		Template template1 = new Template("hi this is {A} and this is {B}");
		assertEquals("an apple", template1.match("hi this is an apple and this is a banana", true).getFilledSlots().getValue("A").toString());
		assertEquals("a banana", template1.match("hi this is an apple and this is a banana", true).getFilledSlots().getValue("B").toString());
	}
	
	
	@Test
	public void testTemplate6() {
		Template template1 = new Template("{anything}");
		assertEquals("bla bla bla", template1.match("bla bla bla", true).getFilledSlots().getValue("anything").toString());
		
		Template template2 = new Template("{anything} is good");
		assertEquals("bla bla bla", template2.match("bla bla bla is good", true).getFilledSlots().getValue("anything").toString());
		assertFalse(template2.match("blo blo", true).isMatching());
		assertFalse(template2.match("bla bla bla is bad", true).getFilledSlots().containsVar("anything"));
		assertTrue(template2.match("blo is good", true).isMatching());
		
		Template template3 = new Template("this could be {anything}");
		assertEquals("pretty much anything", template3.match("this could be pretty much anything", true).getFilledSlots().getValue("anything").toString());
		assertFalse(template3.match("but not this", true).isMatching());
		assertFalse(template3.match("this could beA", true).isMatching());		
		assertFalse(template3.match("this could beA", false).isMatching());		
		assertFalse(template3.match("this could be", true).isMatching());		
		assertFalse(template3.match("this could be", false).isMatching());		
	}
	
	
	@Test
	public void testTemplate7() throws Exception {
		Template template1 = new Template("here we have slot {A} and slot {B}");
		Assignment fillers = new Assignment();
		fillers.addPair("A", "apple");
		fillers.addPair("B", "banana");
		assertEquals("here we have slot apple and slot banana", template1.fillSlots(fillers).getRawString());
		fillers.removePair("B");
		assertEquals("B", template1.fillSlots(fillers).getSlots().iterator().next());	
	}
	
	@Test
	public void testTemplate8() throws Exception {
		Template template = new Template("here we have a test");
		assertFalse(template.match("here we have a test2", true).isMatching());
		assertFalse(template.match("here we have a test2", false).isMatching());
		assertTrue(template.match("here we have a test that is working", false).isMatching());
		assertFalse(template.match("here we have a test that is working", true).isMatching());
	
		Template template2 = new Template("bla");
		assertFalse(template2.match("bla2", false).isMatching());
		assertFalse(template2.match("blabla", false).isMatching());
		assertTrue(template2.match("bla bla", false).isMatching());
		assertFalse(template2.match("bla bla", true).isMatching());
	}
	
	@Test
	public void testTemplate9() {
		Template template1 = new Template("{anything}");
		assertEquals(0, template1.match
				("bla bla bla", true).getBoundaries()[0], 0.0);
		assertEquals(11, template1.match
				("bla bla bla", true).getBoundaries()[1], 0.0);
		Template template2 = new Template("this could be {anything}, right");
		assertEquals(4, template2.match
				("and this could be pretty much anything, right", false).getBoundaries()[0], 0.0);
		assertEquals("and this could be pretty much anything, right".length(), 
				template2.match
				("and this could be pretty much anything, right", false).getBoundaries()[1], 0.0);
		assertEquals(-1, template2.match
				("and this could be pretty much anything", false).getBoundaries()[1], 0.0);
		
		Template template3 = new Template("{}");
		assertEquals(0, template3.getSlots().size());
		assertTrue(template3.match("{}", true).isMatching());
		assertTrue(template3.match("{}", false).isMatching());
		assertFalse(template3.match("something", true).isMatching());
		assertFalse(template3.match("something", false).isMatching());
	}
	
	public void testTemplateOr() {
		Template t1 = new Template("var({X})");
		Template t2 = new Template("var3");
		Template t3 = new Template("bli");
		Template or = new Template(Arrays.asList(t1, t2, t3));
		assertTrue(or.match("var3", true).isMatching());
		assertTrue(or.match("var(blo)", true).isMatching());		
		assertTrue(or.match("bli", true).isMatching());	
		assertFalse(or.match("var3bli", true).isMatching());
		assertFalse(or.match("var", true).isMatching());
	}
	
	
	
	@Test
	public void testTemplateQuick() throws DialException {
		Domain domain = XMLDomainReader.extractDomain("test/domains/quicktest.xml");
		DialogueSystem system = new DialogueSystem(domain);
		system.getSettings().showGUI = false;

		system.startSystem();
		assertEquals(system.getContent("caught").getProb(false), 1.0, 0.01);
		assertEquals(system.getContent("caught2").getProb(true), 1.0, 0.01);
	}
	
	@Test
	public void testTemplateMath() {
		assertEquals(MathUtils.evaluateExpression("1+2"), 3.0, 0.001);
		assertEquals(MathUtils.evaluateExpression("-1.2*3"), -3.6, 0.001);
		Template t = new Template("{X}+2");
		assertEquals(t.fillSlots(new Assignment("X", "3")).toString(), "5.0");
	}
	
	@Test
	public void ComplexRegex() {
		Template t = new Template("a (pizza)? margherita");
		assertTrue(t.match("a margherita", true).isMatching());
		assertTrue(t.match("a pizza margherita", true).isMatching());
		assertFalse(t.match("a pizza", true).isMatching());
		assertTrue(t.match("I would like a margherita", false).isMatching());
		Template t2 = new Template("a (bottle of)? (beer|wine)");
		assertTrue(t2.match("a beer", true).isMatching());
		assertTrue(t2.match("a bottle of wine", true).isMatching());
		assertFalse(t2.match("a bottle of", true).isMatching());
		assertFalse(t2.match("a coke", true).isMatching());
		assertTrue(t2.match("I would like a bottle of beer", false).isMatching());
		Template t3 = new Template("move (a (little)? bit)? (to the)? left");
		assertTrue(t3.match("move a little bit to the left", true).isMatching());
		assertTrue(t3.match("move a bit to the left", true).isMatching());
		assertTrue(t3.match("move to the left", true).isMatching());
		assertTrue(t3.match("move a little bit left", true).isMatching());
		assertFalse(t3.match("move a to the left", true).isMatching());
		Template t4 = new Template("I want beer(s)?");
		assertTrue(t4.match("I want beer", true).isMatching());
		assertTrue(t4.match("I want beers", true).isMatching());
		assertFalse(t4.match("I want beer s", true).isMatching());
		Template t5 = new Template("(beer(s)?|wine)");
		assertTrue(t5.match("beer", true).isMatching());
		assertTrue(t5.match("beers", true).isMatching());
		assertTrue(t5.match("wine", true).isMatching());
		assertFalse(t5.match("wines", true).isMatching());
		assertFalse(t5.match("beer wine", true).isMatching());
	}
	
	@Test
	public void testDouble() {
		Template t= new Template("MakeOrder({Price})");
		assertTrue(t.match("MakeOrder(179)", true).isMatching());
		assertTrue(t.match("MakeOrder(179.0)", true).isMatching());
		assertFalse(t.match("MakkeOrder(179.0)", true).isMatching());
		assertFalse(t.match("MakkeOrder()", true).isMatching());
	}
	
	@Test
	public void testMatchInString() {
		Template t = new Template("{X}th of March");
		assertTrue(t.match("20th of March", true).isMatching());
		assertTrue(t.match("on the 20th of March", false).isMatching());
		assertFalse(t.match("20 of March", true).isMatching());
	}
}
