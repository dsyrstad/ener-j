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
package com.vscorp.ui.swing;

import java.awt.AWTError;
import java.lang.reflect.Method;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** 
 This class supports time consuming operations when an event occurs on
 a Swing component. The application's event handler can call the static
 <tt>runTask()</tt> method so that the operation executes on another thread.
 By doing this, the Swing event thread is not blocked from processing events.
 Optionally, the application may specify a "finishMethod" that executes
 on the Swing event thread after the primary operation has completed.
 <p>This class has advantages over classes such as <tt>SwingWorker</tt>
 (see the Swing Connection at http://java.sun.com):
 <ul>
 <li>it is simplier to use because it uses Reflection to invoke the specified
 method;</li>
 <li>the resulting code is clearer and more direct;</li>
 <li>the task and finish methods are invoked directly on the specified target
 object. Typically this object will be the same as the invoker of the runTask()
 method;</li>
 <li>no inner classes are required -- this improves class loading speed and
 shrinks the size of the application's jar file.</li>
 </ul>
 Here is an example illustrating the use of <tt>VSBackgroundTask</tt> as
 well as <tt>VSJFrame</tt>, <tt>VSSwingUtil.invokeLater()</tt>, and the
 event redirectors defined in <tt>com.vscorp.ui.swing.redirector</tt>.
 Not that no inner classes or separate event handlers are used.
 <p><tt>import javax.swing.*;
 <br>import javax.swing.event.*;
 <br>import java.awt.*;
 <br>import java.awt.event.*;
 <br>import com.vscorp.ui.swing.*;
 <br>import com.vscorp.ui.swing.redirector.*;
 <p>public class TestBkg extends VSJFrame
 <br>{
 <br>&nbsp;&nbsp; private JButton button;
 <p>&nbsp;&nbsp; //----------------------------------------------------------------------
 <br>&nbsp;&nbsp; TestBkg()
 <br>&nbsp;&nbsp; {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; super("Test Background Task");
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; addWindowListener(new WindowRedirector(this,
 null, null, "close", null, null, null, null));
 <br>&nbsp;
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Container contentPane = getContentPane();
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; button = new JButton("Do Work");
 <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; button.addActionListener(new ActionRedirector(this,
 "doWorkAction"));
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; contentPane.add(button, BorderLayout.CENTER);
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; pack();
 <br>&nbsp;&nbsp; }
 <p>&nbsp;&nbsp; //----------------------------------------------------------------------
 <br>&nbsp;&nbsp; private void close(WindowEvent anEvent)
 <br>&nbsp;&nbsp; {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; if (!this.isBusy()) {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; setVisible(false);
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; dispose();
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; System.exit(1);
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }
 <br>&nbsp;&nbsp; }
 <p>&nbsp;&nbsp; //----------------------------------------------------------------------
 <br>&nbsp;&nbsp; private void doWorkAction(ActionEvent anEvent)
 <br>&nbsp;&nbsp; {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; setBusy(true);
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; VSBackgroundTask.runTask(this, anEvent,
 "doWorkTask", "doWorkDone");
 <br>&nbsp;&nbsp; }
 <p>&nbsp;&nbsp; //----------------------------------------------------------------------
 <br>&nbsp;&nbsp; /** Called from a background thread. *<tt>/
 <br>&nbsp;&nbsp; private void doWorkTask(Object anObj)
 <br>&nbsp;&nbsp; {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ActionEvent event = (ActionEvent)anObj;
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; // Invoke this from the Swing Event
 thread.
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; VSSwingUtil.invokeLater(this, "Doing
 work", "changeButton");
 <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; try {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Thread.sleep(5000L);
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; catch (InterruptedException e) {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }
 <br>&nbsp;&nbsp; }
 <br>&nbsp;
 <br>&nbsp;&nbsp; //----------------------------------------------------------------------
 <br>&nbsp;&nbsp; /** Called from the Swing event thread. *<tt>/
 <br>&nbsp;&nbsp; private void doWorkDone(Object anObj)
 <br>&nbsp;&nbsp; {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; button.setText("Work Done");
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; setBusy(false);
 <br>&nbsp;&nbsp; }
 <br>&nbsp;
 <br>&nbsp;&nbsp; //----------------------------------------------------------------------
 <br>&nbsp;&nbsp; private void changeButton(Object anObj)
 <br>&nbsp;&nbsp; {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; button.setText((String)anObj);
 <br>&nbsp;&nbsp; }
 <br>&nbsp;
 <br>&nbsp;&nbsp; //----------------------------------------------------------------------
 <br>&nbsp;&nbsp; public static void main(String[] args) throws Exception
 <br>&nbsp;&nbsp; {
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; TestBkg bkg = new TestBkg();
 <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; bkg.setVisible(true);
 <br>&nbsp;&nbsp; }
 <br>}</tt>
 <p>@author Daniel A. Syrstad
 */
public class VSBackgroundTask extends Thread
{
    private Object targetObj;
    private Object parameter;
    private Method taskMethod;
    private Method finishMethod;


    protected VSBackgroundTask(Object aTargetObj, Object aParameter, String aTaskMethodName, String aFinishMethodName)
    {
        targetObj = aTargetObj;
        parameter = aParameter;
        try {
            taskMethod = VSSwingUtil.resolveMethod(aTargetObj, aTaskMethodName, new Class[] { Object.class });
            taskMethod.setAccessible(true);
            if (aFinishMethodName != null) {
                finishMethod = VSSwingUtil.resolveMethod(aTargetObj, aFinishMethodName, new Class[] { Object.class });
                finishMethod.setAccessible(true);
            }
            else
                finishMethod = null;
        }
        catch (Exception e) {
            throw new AWTError("Method exception:" + e); // Unchecked exception
        }
    }


    /** Implements Thread.run() and executes the task method followed by the
     * finish method. The finish method is invoked from the Swing event thread.
     */
    public void run()
    {
        try {
            VSSwingUtil.invokeMethod(taskMethod, targetObj, new Object[] { parameter });
        }
        catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(null, new JLabel(t.toString()), "Unhandled Exception", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if (finishMethod != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                        VSSwingUtil.invokeMethod(finishMethod, targetObj, new Object[] { parameter });
                    }
                });
            }
        }
    }


    /**
     * Runs a task on a thread separate from the Swing Event thread.
     * See the description of this class for more information
     * 
     * @param aTargetObj the object on which aTaskMethodName and aFinishMethodName will be invoked
     * @param aParameter a parameter to pass to aTaskMethodName and aFinishMethodName
     * @param aTaskMethodName the name of the method on aTargetObj which performs the background task
     * @param aFinishMethodName the name of the method on aTargetObj which is executed after aTaskMethodName 
     *   completes (via the Swing Event thread). This parameter may be null.
     */
    static public void runTask(Object aTargetObj, Object aParameter, String aTaskMethodName, String aFinishMethodName)
    {
        if (aTargetObj == null || aTaskMethodName == null)
            return;

        VSBackgroundTask task = new VSBackgroundTask(aTargetObj, aParameter, aTaskMethodName, aFinishMethodName);
        task.start();
    }

}