/*+
 * Copyright 1998 Visual Systems Corporation
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Neither the name "Visual Systems" nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY VISUAL SYSTEMS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL VISUAL SYSTEMS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *-
 */
package com.vscorp.ui.swing.redirector;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

/** Generates the Redirector subclasses by reflecting on the listener class names
 * supplied as arguments.
 *
 * @author Daniel A. Syrstad
 */
public class GenRedirectors {
   static private final String copr =
      " * Copyright 1998 Visual Systems Corporation\n" +
      " * All rights reserved.\n" +
      " * Redistribution and use in source and binary forms, with or without\n" +
      " * modification, are permitted provided that the following conditions\n" +
      " * are met:\n" +
      " * 1. Redistributions of source code must retain the above copyright\n" +
      " *    notice, this list of conditions and the following disclaimer.\n" +
      " * 2. Neither the name \"Visual Systems\" nor the names of its contributors\n" +
      " *    may be used to endorse or promote products derived from this software\n" +
      " *    without specific prior written permission.\n" +
      " *\n" +
      " * THIS SOFTWARE IS PROVIDED BY VISUAL SYSTEMS \"AS IS\" AND\n" +
      " * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE\n" +
      " * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE\n" +
      " * ARE DISCLAIMED.  IN NO EVENT SHALL VISUAL SYSTEMS BE LIABLE\n" +
      " * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL\n" +
      " * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS\n" +
      " * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)\n" +
      " * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT\n" +
      " * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY\n" +
      " * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF\n" +
      " * SUCH DAMAGE.";

   private static final String[] interfaces = new String[] {
      "java.awt.dnd.DragGestureListener",
      "java.awt.dnd.DragSourceListener",
      "java.awt.dnd.DropTargetListener",
      "java.awt.event.ActionListener",
      "java.awt.event.AdjustmentListener",
      "java.awt.event.ComponentListener",
      "java.awt.event.ContainerListener",
      "java.awt.event.FocusListener",
      "java.awt.event.InputMethodListener",
      "java.awt.event.ItemListener",
      "java.awt.event.KeyListener",
      "java.awt.event.MouseListener",
      "java.awt.event.MouseMotionListener",
      "java.awt.event.TextListener",
      "java.awt.event.WindowListener",
      "javax.swing.event.AncestorListener",
      "javax.swing.event.CaretListener",
      "javax.swing.event.CellEditorListener",
      "javax.swing.event.ChangeListener",
      "javax.swing.event.HyperlinkListener",
      "javax.swing.event.InternalFrameListener",
      "javax.swing.event.ListDataListener",
      "javax.swing.event.ListSelectionListener",
      "javax.swing.event.MenuDragMouseListener",
      "javax.swing.event.MenuKeyListener",
      "javax.swing.event.MenuListener",
      "javax.swing.event.MouseInputListener",
      "javax.swing.event.PopupMenuListener",
      "javax.swing.event.TableColumnModelListener",
      "javax.swing.event.TableModelListener",
      "javax.swing.event.TreeExpansionListener",
      "javax.swing.event.TreeModelListener",
      "javax.swing.event.TreeSelectionListener",
      "javax.swing.event.TreeWillExpandListener",
      "javax.swing.event.UndoableEditListener",
   };

   private static String packageName = "com.vscorp.ui.swing.redirectors";

	//----------------------------------------------------------------------
	static void generate(String anInterfaceName) throws Exception {
		String baseIntfName = anInterfaceName.substring(anInterfaceName.lastIndexOf('.') + 1);
		String listenerName = baseIntfName.substring(0, baseIntfName.lastIndexOf("Listener"));
		String redirectorName = listenerName + "Redirector";
		PrintStream out = new PrintStream(new FileOutputStream(redirectorName + ".java"));

		Class intf = Class.forName(anInterfaceName);

		Method[] methods = intf.getMethods();

      out.println("/*");
      out.println(copr);
      out.println("*/\n");
		out.println("package " + packageName + ";\n");
		out.println("import java.awt.event.*;");
		out.println("import javax.swing.event.*;");
		out.println("import java.lang.reflect.*;");

		out.println("\n/** Redirector for " + anInterfaceName + ".");
		out.println(" * Automatically generated by GenRedirectors. Do not modify directly.");
		out.println(" */\n");
		out.println("public class " + redirectorName + " extends EventRedirector implements " + anInterfaceName + " {");

		// Generate the Method fields
		for (int i = 0; i < methods.length; i++)
			out.println("\tprivate Method " + methods[i].getName() + "Method;");

		out.println("\n\tpublic " + redirectorName + "(Object anEventReceiver, ");
		for (int i = 0; i < methods.length; i++) {
			out.print("\t\t\tString " + methods[i].getName() + "MethodName");
			if ((i + 1) == methods.length)
				out.println(") {");
			else
				out.println(", ");
		}

		out.println("\t\tsuper(anEventReceiver);");
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName();
			String paramType = methods[i].getParameterTypes()[0].getName();
			out.println("\t\t" + methodName + "Method = getEventMethod(" + methodName + "MethodName, " + paramType + ".class);");
		}

		out.println("\t}");

		out.println("\n\t// Interface Methods:");
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName();
			String paramType = methods[i].getParameterTypes()[0].getName();
			//String paramType = "EventObject";
			out.println("\tpublic void " + methodName + '(' +
						paramType + " anEvent) {\n" +
						"\t\tcallEventMethod(" + methodName + "Method, anEvent);\n\t}");
		}

		out.println("}");
		out.close();
	}

	//----------------------------------------------------------------------
	public static void main(String[] args) throws Exception {
	   int idx = 0;
	   if (args[idx].equals("-p")) {
	      ++idx;
	      packageName = args[idx];
	      ++idx;
	   }

		// Reflect on each one of the interfaces.
		for (int i = 0; i < interfaces.length; i++) {
		   if (!interfaces[i].endsWith(".AWTEventListener") &&
		       !interfaces[i].endsWith(".DocumentListener"))
			   generate(interfaces[i]);
		}
	}

}
