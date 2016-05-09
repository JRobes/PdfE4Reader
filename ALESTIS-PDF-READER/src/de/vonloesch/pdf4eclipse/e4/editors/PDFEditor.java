 
package de.vonloesch.pdf4eclipse.e4.editors;

import javax.inject.Inject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.demo.simpleide.editor.internal.ExternalFileDocumentInput;
import org.eclipse.e4.demo.simpleide.editor.internal.NewFileDocumentInput;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.osgi.service.prefs.Preferences;

import com.alestis.pdfreader.PDFPageViewer;
import com.alestis.pdfreader.preferences.PreferenceConstants;

import de.vonloesch.pdf4eclipse.model.IPDFFile;
import de.vonloesch.pdf4eclipse.model.IPDFPage;
import de.vonloesch.pdf4eclipse.model.PDFFactory;



//import de.vonloesch.pdf4eclipse.PDFPageViewer;
//import de.vonloesch.pdf4eclipse.editors.StatusLinePageSelector;
//import de.vonloesch.pdf4eclipse.model.IPDFFile;
//import de.vonloesch.pdf4eclipse.outline.PDFFileOutline;

public class PDFEditor {

	public static final String ID = "de.vonloesch.pdf4eclipse.editors.PDFEditor"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "PDFViewer.editors.contextid"; //$NON-NLS-1$

	public static final int FORWARD_SEARCH_OK = 0;
	public static final int FORWARD_SEARCH_NO_SYNCTEX = -1;
	public static final int FORWARD_SEARCH_FILE_NOT_FOUND = -2;
	public static final int FORWARD_SEARCH_POS_NOT_FOUND = -3;
	public static final int FORWARD_SEARCH_UNKNOWN_ERROR = -4;
	
	private static final float MOUSE_ZOOMFACTOR = 0.2f;
	
	private static final int SCROLLING_WAIT_TIME = 200;

	static final String PDFPOSITION_ID = "PDFPosition"; //$NON-NLS-1$
	
	public PDFPageViewer pv;
	private File file;

	private IPDFFile f;
	private ScrolledComposite sc;
	int currentPage;
	//private PDFFileOutline outline;
	//private StatusLinePageSelector position;
	
	private Listener mouseWheelPageListener;
	private boolean isListeningForMouseWheel = true;

	private Cursor cursorHand;
	private Cursor cursorArrow;
	int r;
	
	@Inject
	EModelService modelService;
	@Inject
	MPart parte;
	@Inject
	MApplication app;

		
	@Inject
	public PDFEditor() {
		currentPage = 1;
	
	    
		/*
		Preferences instanceScopePreferences = InstanceScope.INSTANCE.getNode("ALESTIS-PDF-READER");
		Preferences sub1 = instanceScopePreferences.node("nodolll1");
		sub1.put("Key1", "mivalor232323");
		Preferences sub2 = instanceScopePreferences.node("");
		System.out.println("Recogiendo valor pseudoContinuousScrolling\t"+sub2.get("pseudoContinuousScrolling", "falsoooll"));
		System.out.println("Recogiendo valor pseudoContinuousScrolling\t"+sub2.get(PreferenceConstants.PDF_RENDERER, "falsoooll----2"));
		System.out.println("Default-Scope");
		//System.out.println("Recogiendo valor pseudoContinuousScrolling\t"+sub2.get(PreferenceConstants.PDF_RENDERER, "falsoooll----2"));
		Preferences defaultScopePreferences = DefaultScope.INSTANCE.getNode("ALESTIS-PDF-READER");
		Preferences defaultNode = defaultScopePreferences.node("");
		System.out.println("Recogiendo valor pseudoContinuousScrolling en Default\t"+defaultNode.get(PreferenceConstants.PSEUDO_CONTINUOUS_SCROLLING, "Este no puede ser nunca falso"));
		System.out.println("Recogiendo valor pseudoContinuousScrolling en Default\t"+defaultNode.get(PreferenceConstants.PDF_RENDERER, "Ni este puede ser falso"));
		
		
		System.out.println("Recogiendo valor pseudoContinuousScrolling en Default sin node\t"+defaultScopePreferences.get(PreferenceConstants.PSEUDO_CONTINUOUS_SCROLLING, "Este no puede ser nunca falso"));
		System.out.println("Recogiendo valor pseudoContinuousScrolling en Default sin node\t"+defaultScopePreferences.get(PreferenceConstants.PDF_RENDERER, "Ni este puede ser falso"));
		*/
	    
	    
	    
	    
	}

	private void getFile(MPart part) {
		if(file==null){
			file = new File("C:\\BORRA\\impreso_visado.pdf");

			//file = new File("C:\\RRHH\\solicitud vacaciones 23-03-2016.pdf");
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	//NewFileDocumentInput newFileIn = new NewFileDocumentInput(file);
	    	System.out.println("El archivo es null y el nombre dado al documento es:\t"+file.getName());
	    	part.setLabel(file.getName());
	    	
	    }
	    else{
	    	System.out.println("file es distinto de null, con nombre");
	    	//ExternalFileDocumentInput fileIn = new ExternalFileDocumentInput(file);
	    	
	    	part.setLabel(file.getName());
	    	
	    	
	    }
		f = null;
		
		Preferences preferences = InstanceScope.INSTANCE.getNode("ALESTIS-PDF-READER");
		
		
		//IEclipsePreferences prefs = (new InstanceScope()).getNode(de.vonloesch.pdf4eclipse.Activator.PLUGIN_ID);
		//r = preferences.getInt(PreferenceConstants.PDF_RENDERER, PDFFactory.STRATEGY_SUN);

		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent, MPart part) {
		file  = (File) part.getTransientData().get("File Name") ;
		if(file instanceof File){
			System.out.println("El archivo es instanceof File...manda  cojones");
			System.out.println(file.getName());
		}
	    getFile(part);
		//parte.setCloseable(true);
		
		
		
		
		try {
			f = PDFFactory.openPDFFile(file, 1);//only PDFSUN
			//TODO 
			//Modify openPDFFile to be able to open PDF files with JPedal renderer 
			//f = PDFFactory.openPDFFile(file, r);
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		
		
		cursorHand = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
		cursorArrow = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
		
		parent.setLayout(new FillLayout());
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CENTER);
		pv = new PDFPageViewer(sc);
		//pv = new PDFPageViewerAWT(sc, this);
		sc.setContent(pv);
				// Speed up scrolling when using a wheel mouse
		ScrollBar vBar = sc.getVerticalBar();
		vBar.setIncrement(10);
		//isListeningForMouseWheel = true;
		
		mouseWheelPageListener = new Listener() {
			
			//last time the page number changed due to a scrolling event
			long lastTime;
			
			@Override
			public void handleEvent(Event e) {
				long time = e.time & 0xFFFFFFFFL;
				
				//If a scrolling event occurs within a very short period of time
				//after the last page change discard it. This avoids "overscrolling"
				//the beginning of the next page
				if (time - lastTime < SCROLLING_WAIT_TIME) {
					e.doit = false;
					return;
				}
				
				Point p = sc.getOrigin();
				
				int height = sc.getClientArea().height;
				int pheight = sc.getContent().getBounds().height;

				if (p.y >= pheight - height && e.count < 0) {
					//We are at the end of the page
					if (currentPage < f.getNumPages()) {
						showPage(currentPage + 1);
						setOrigin(sc.getOrigin().x, 0);
						e.doit = false;
						lastTime = time;
					}
				} else if (p.y <= 0 && e.count > 0) {
					//We are at the top of the page
					if (currentPage > 1) {
						showPage(currentPage - 1);
						setOrigin(sc.getOrigin().x, pheight);
						e.doit = false;
						lastTime = time;
					}
				}
				
			}
		};
		//pv.addListener(SWT.MouseWheel, mouseWheelPageListener);
		
		pv.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {
				if((e.stateMask & SWT.CTRL) > 0) {
					Point o = getOrigin();
					Point oldSize = pv.getSize();
					pv.setZoomFactor(Math.max(pv.getZoomFactor() + MOUSE_ZOOMFACTOR*(float)e.count/10, 0));
					int mx = Math.round((float)pv.getSize().x * ((float)e.x / oldSize.x)) - (e.x-o.x);
					int my = Math.round((float)pv.getSize().y * ((float)e.y / oldSize.y)) - (e.y-o.y);
					setOrigin(mx,my);
					return;
				}
				
			}
		});	
		
		
	pv.addMouseListener(new MouseListener() {
			
			Point start;
			MouseMoveListener mml = new MouseMoveListener() {
				
				@Override
				public void mouseMove(MouseEvent e) {
					if((e.stateMask & SWT.BUTTON2) == 0) {
						pv.removeMouseMoveListener(this);
						pv.setCursor(cursorArrow);
						return;
					}
					Point o = sc.getOrigin();
					sc.setOrigin(o.x-(e.x-start.x), o.y-(e.y-start.y));
				}
			};
			
			@Override
			public void mouseUp(MouseEvent e) {
				if(e.button != 2)
					return;
				pv.removeMouseMoveListener(mml);
				pv.setCursor(cursorArrow);
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				if(e.button != 2)
					return;
				start = new Point(e.x, e.y);
				pv.addMouseMoveListener(mml);
				pv.setCursor(cursorHand);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
		});		
		
		pv.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int height = sc.getClientArea().height;
				int pInc = 3* height / 4;
				int lInc = height / 20;
				int hInc = sc.getClientArea().width / 20;
				int pheight = sc.getContent().getBounds().height;
				Point p = sc.getOrigin();
				if (e.keyCode == SWT.PAGE_DOWN) {
					if (p.y < pheight - height) {
						int y = p.y + pInc;
						if (y > pheight - height) {
							y = pheight - height;
						}
						sc.setOrigin(sc.getOrigin().x, y);
					}
					else {
						//We are at the end of the page
						if (currentPage < f.getNumPages()) {
							showPage(currentPage + 1);
							setOrigin(sc.getOrigin().x, 0);
						}
					}
				}
				else if (e.keyCode == SWT.PAGE_UP) {
					if (p.y > 0) {
						int y = p.y - pInc;
						if (y < 0) y = 0;
						sc.setOrigin(sc.getOrigin().x, y);
					}
					else {
						//We are at the top of the page
						if (currentPage > 1) {
							showPage(currentPage - 1);
							setOrigin(sc.getOrigin().x, pheight);
						}
					}					
				}
				else if (e.keyCode == SWT.ARROW_DOWN) {
					if (p.y < pheight - height) {
						sc.setOrigin(sc.getOrigin().x, p.y + lInc);
					}					
				}
				else if (e.keyCode == SWT.ARROW_UP) {
					if (p.y > 0) {
						int y = p.y - lInc;
						if (y < 0) y = 0;
						sc.setOrigin(sc.getOrigin().x, y);
					}					
				}
				else if (e.keyCode == SWT.ARROW_RIGHT) {
					if (p.x < sc.getContent().getBounds().width - sc.getClientArea().width) {
						sc.setOrigin(p.x + hInc, sc.getOrigin().y);
					}
				}
				else if (e.keyCode == SWT.ARROW_LEFT) {
					if (p.x > 0) {
						int x = p.x - hInc;
						if (x < 0) x = 0;
						sc.setOrigin(x, sc.getOrigin().y);
					}					
				}
				else if (e.keyCode == SWT.HOME) {
					showPage(1);
					setOrigin(sc.getOrigin().x, 0);
				}
				else if (e.keyCode == SWT.END) {
					showPage(f.getNumPages());
					setOrigin(sc.getOrigin().x, pheight);
				}	

			}
		});
		
		
	
		if (f != null) {
			showPage(currentPage);
		}
	}
	
	public void showPage(int pageNr) {
		if (pageNr < 1) pageNr = 1;
		if (pageNr > f.getNumPages()) pageNr = f.getNumPages();
		IPDFPage page = f.getPage(pageNr);
		currentPage = pageNr;
		pv.showPage(page);
		updateStatusLine();
	}
	
	private void updateStatusLine() {
		//position.setPageInfo(currentPage, f.getNumPages());
	}
	
	Point getOrigin() {
		if (!sc.isDisposed()) return sc.getOrigin();
		else return null;
	}
	void setOrigin(int x, int y) {
		sc.setRedraw(false);
		sc.setOrigin(x, y);
		sc.setRedraw(true);
	}
	

	void setOrigin(Point p) {
		sc.setRedraw(false);
		if (p != null) sc.setOrigin(p);
		sc.setRedraw(true);
	}
	
	@PreDestroy
	public void dispose() {
		
		
		if (sc != null) sc.dispose();
		if (pv != null) pv.dispose();
		//if (outline != null) outline.dispose();
		if (cursorArrow != null) cursorArrow.dispose();
		if (cursorHand != null) cursorHand.dispose();
		
		//ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		//if (position != null) position.removePageChangeListener(this);
		
       // IEclipsePreferences prefs = (new InstanceScope()).getNode(de.vonloesch.pdf4eclipse.Activator.PLUGIN_ID);
		//prefs.removePreferenceChangeListener(this);
		
		
		MPartStack editorStack = (MPartStack) modelService.find("alestis-pdf-reader.partstack.pdf", app);
		editorStack.getChildren().remove(parte);
		
		if (f != null) f.close();
		f = null;
		pv = null;
	}
	
	@Inject
	@Optional
	public void trackPseudoContinuousScrollingPreference(@Preference(nodePath="",value="pseudoContinuousScrolling") String pref){
		boolean newValue = Boolean.parseBoolean(pref);
		System.out.println("Ha cambiado  las preferencia pseudoContinuousScrolling:\t"+newValue);
		//I do not know why this happens, but getNewValue() returns null instead of true
		if (pref == null) newValue = true;
		
		if (isListeningForMouseWheel && newValue == false) {
			pv.removeListener(SWT.MouseWheel, mouseWheelPageListener);
			isListeningForMouseWheel = false;
		}
		else if (!isListeningForMouseWheel && newValue == true) {
			pv.addListener(SWT.MouseWheel, mouseWheelPageListener);
			isListeningForMouseWheel = true;
		}
		
		
		
	}
	
}