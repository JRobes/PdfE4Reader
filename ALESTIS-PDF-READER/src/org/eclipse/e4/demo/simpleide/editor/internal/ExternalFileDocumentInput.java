package org.eclipse.e4.demo.simpleide.editor.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.demo.simpleide.editor.IDocumentInput;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;

public class ExternalFileDocumentInput extends AbstractInput implements IDocumentInput {
	private File file;
	private IDocument document;

	private static final int DEFAULT_FILE_SIZE = 15 * 1024;

	public ExternalFileDocumentInput(File file) {
		this.file = file;
	}

	public IStatus save() {
		System.err.println("Starting save");
		
		String encoding = "UTF-8";
			
		Charset charset= Charset.forName(encoding);
		CharsetEncoder encoder = charset.newEncoder();
		
		byte[] bytes;
		ByteBuffer byteBuffer;
		try {
			byteBuffer = encoder.encode(CharBuffer.wrap(document.get()));
			if (byteBuffer.hasArray())
				bytes= byteBuffer.array();
			else {
				bytes= new byte[byteBuffer.limit()];
				byteBuffer.get(bytes);
			}
			ByteArrayInputStream stream= new ByteArrayInputStream(bytes, 0, byteBuffer.limit());
			
			//ESTO HAY QUE CAMBIAR CUANDO SE QUIERE HACER UN SAVE
			try {
				((IFile) file).setContents(stream, true, true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setDirty(false);
			
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println("Saving done");
		
		return null;
	}

	public IDocument getDocument() {
		if (document == null) {
			Document document = new Document();
			Reader in = null;

			InputStream contentStream = null;
			try {
				contentStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("el archivo file es = a null y salta FileNotFoundException");
				e.printStackTrace();
			}
			//OBTIENE UN INPUT STREAM DEL ARCHIVO Y DEVUELVE NULL SI NO LO ENCUENTRA
			//A MODIFICAR
			/*
			InputStream contentStream = null;
			try {
				contentStream = file.getContents();
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (contentStream == null) {
				return null;
			}
			 */
			try {
				String encoding = "UTF-8";
				in = new BufferedReader(new InputStreamReader(contentStream,
						encoding), DEFAULT_FILE_SIZE);
				StringBuffer buffer = new StringBuffer(DEFAULT_FILE_SIZE);
				char[] readBuffer = new char[2048];
				int n = in.read(readBuffer);
				while (n > 0) {
					buffer.append(readBuffer, 0, n);
					n = in.read(readBuffer);
				}

				document.set(buffer.toString());
				this.document = document;
				document.addDocumentListener(new IDocumentListener() {
					
					public void documentChanged(DocumentEvent event) {
						setDirty(true);
					}
					
					public void documentAboutToBeChanged(DocumentEvent event) {
						
					}
				});
			} catch (IOException x) {
				x.printStackTrace();
			} finally {
				try {
					if (in != null)
						in.close();
					else
						contentStream.close();
				} catch (IOException x) {
				}
			}
		}

		return document;
	}
}
