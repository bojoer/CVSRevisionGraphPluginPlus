/*
 * CVS Revision Graph Plus IntelliJ IDEA Plugin
 *
 * Copyright (C) 2011, Łukasz Zieliński
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cvstoolbox.graph;

import com.intellij.cvsSupport2.history.CvsFileRevision;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.ui.JBColor;
import info.clearthought.layout.TableLayout;
import org.cvstoolbox.graph.revisions.BranchRevision;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CVSRevisionGraphCellViewFactory extends DefaultCellViewFactory {
  public static final String DEAD_STATE = "dead";
  public static final int CELL_MARGIN = 3;

  protected Color _branchColorRegular = new Color(184,230,230);
  protected Color _branchColorDarcula = new Color(49, 108, 108);
  protected Color _branchColor = new JBColor(_branchColorRegular, _branchColorDarcula);
  protected Color _branchSelectedColorRegular = new Color(51,128,128);
  protected Color _branchSelectedColorDarcula = new Color(109, 160, 181);
  protected Color _branchSelectedColor = new JBColor(_branchSelectedColorRegular, _branchSelectedColorDarcula);
  protected Color _revisionColorRegular = new Color(185,230,184);
  protected Color _revisionColorDarcula = new Color(46, 93, 44);  
  protected Color _revisionColor = new JBColor(_revisionColorRegular, _revisionColorDarcula);
  protected Color _revisionSelectedColorRegular = new Color(53,128,51);
  protected Color _revisionSelectedColorDarcula = new Color(82, 165, 82);
  protected Color _revisionSelectedColor = new JBColor(_revisionSelectedColorRegular, _revisionSelectedColorDarcula);
  protected Color _revisionDeletedColorRegular = new Color(218,230,218);
  protected Color _revisionDeletedColorDarcula = new Color(70, 82, 75);
  protected Color _revisionDeletedColor = new JBColor(_revisionDeletedColorRegular, _revisionDeletedColorDarcula);
  protected Color _revisionDeletedSelectedColorRegular = new Color(109,128,108);
  protected Color _revisionDeletedSelectedColorDarcula = new Color(193, 205, 193);
  protected Color _revisionDeletedSelectedColor = new JBColor(_revisionDeletedSelectedColorRegular, 
                                                                _revisionDeletedSelectedColorDarcula);

  protected RevisionVertexRenderer _revisionCellRenderer = null;
  protected BranchVertexRenderer _branchCellRenderer = null;
  protected String _currentRevision = null;
  protected Project _project = null;
  protected boolean _showTags = true;
  protected boolean _showTagFilter = false;
  protected String _tagFilter = null;

  public CVSRevisionGraphCellViewFactory(Project project)
  {
    _project = project;
    _revisionCellRenderer = new RevisionVertexRenderer();
    _branchCellRenderer = new BranchVertexRenderer();
  }

  public void setCurrentRevision(String currentRevision)
  {
    _currentRevision = currentRevision;
  }

  public boolean isShowTags()
  {
    return(_showTags);
  }

  public void setShowTags(boolean showTags)
  {
    _showTags = showTags;
  }

  public String getTagFilter()
  {
    return(_tagFilter);
  }

  public void setTagFilter(String tagFilter)
  {
    _tagFilter = tagFilter;
  }

  public boolean isShowTagFilter()
  {
    return _showTagFilter;
  }

  public void setShowTagFilter(boolean showTagFilter)
  {
    _showTagFilter = showTagFilter;
  }

  protected VertexView createVertexView(Object cell)
  {
    DefaultGraphCell dgc = (DefaultGraphCell)cell;
    Object userObj = dgc.getUserObject();
    if(userObj instanceof VcsFileRevision)
      return(new RevisionVertexView(dgc));
    else if(userObj instanceof BranchRevision)
      return(new BranchVertexView(dgc));
    else
      return(null);
  }

  protected class BranchVertexView extends VertexView {
    public BranchVertexView(Object cell)
    {
      super(cell);
    }

    public CellViewRenderer getRenderer()
    {
      return(_branchCellRenderer);
    }
  }

  protected class RevisionVertexView extends VertexView {
    public RevisionVertexView(Object cell)
    {
      super(cell);
    }

    public CellViewRenderer getRenderer()
    {
      return(_revisionCellRenderer);
    }
  }

   protected class BranchVertexRenderer extends VertexRenderer {
    protected JPanel _panel = null;
    protected JLabel _name = null;
    protected JLabel _revision = null;

    public BranchVertexRenderer()
    {
      double tableSizes[][] = {{TableLayout.FILL},{TableLayout.PREFERRED,TableLayout.PREFERRED}};
      TableLayout tl = new TableLayout(tableSizes);
      tl.setHGap(5);
      tl.setVGap(5);
      _panel = new JPanel(tl);
      _panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(JBColor.black,1),BorderFactory.createEmptyBorder(CELL_MARGIN,CELL_MARGIN,CELL_MARGIN,CELL_MARGIN)));
      _name = new JLabel();
      _panel.add(_name,"0,0,c,f");
      _revision = new JLabel();
      _panel.add(_revision,"0,1,c,f");
    }

    public Component getRendererComponent(JGraph graph,CellView view,boolean selected,boolean focus,boolean preview)
    {
      DefaultGraphCell cell = (DefaultGraphCell)view.getCell();
      BranchRevision branchRevision = (BranchRevision)cell.getUserObject();
      _name.setText(branchRevision.getName());
      if(branchRevision.getRevision().equals("1")) {
        _revision.setText("");
        _revision.setVisible(false);
      } else {
        _revision.setText(branchRevision.getRevision());
        _revision.setVisible(true);
      }
      if(selected) {
        _panel.setBackground(_branchSelectedColor);
        _name.setForeground(JBColor.white);
        _revision.setForeground(JBColor.white);
      } else {
        _panel.setBackground(_branchColor);
        _name.setForeground(JBColor.black);
        _revision.setForeground(JBColor.black);
      }
      return(_panel);
    }
  }

  protected class RevisionVertexRenderer extends VertexRenderer {
    protected JPanel _panel = null;
    protected JLabel _author = null;
    protected JLabel _revision = null;
    protected JSeparator _sep = null;
    protected JTextArea _ta = null;
    protected TableLayout _tl = null;

    public RevisionVertexRenderer()
    {
      double tableSizes[][] = {{TableLayout.FILL,TableLayout.FILL},{TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED}};
      _tl = new TableLayout(tableSizes);
      _tl.setHGap(5);
      _panel = new JPanel(_tl);
      _revision = new JLabel();
      _panel.add(_revision,"0,0,l,f");
      _author = new JLabel();
      _panel.add(_author,"1,0,r,f");
      _sep = new JSeparator(JSeparator.HORIZONTAL);
      _panel.add(_sep,"0,1,1,1,f,f");
      _ta = new JTextArea();
      _panel.add(_ta,"0,2,1,2");
    }

    public Component getRendererComponent(JGraph graph,CellView view,boolean selected,boolean focus,boolean preview)
    {
      DefaultGraphCell cell = (DefaultGraphCell)view.getCell();
      VcsFileRevision rev = (VcsFileRevision)cell.getUserObject();
      boolean isDead = false;
      ArrayList<String> revLabels = null;
      if((_showTags) && (rev instanceof CvsFileRevision)) {
        CvsFileRevision cvsRev = (CvsFileRevision)rev;
        if(cvsRev.getState().equals(DEAD_STATE))
          isDead = true;
        Collection<String> revLabelsC = cvsRev.getTags();
        if(revLabelsC != null) {
          revLabels = new ArrayList<String>(revLabelsC);
          Collections.sort(revLabels);
        }
        //Filter labels based on configuration
        if((revLabels != null) && (_tagFilter != null) && (_tagFilter.length() > 0)) {
          for(int i = (revLabels.size() - 1); i >= 0; i--) {
            String revLabel = revLabels.get(i);
            boolean matches = revLabel.matches(_tagFilter);
            if(_showTagFilter ? !matches : matches)
              revLabels.remove(i);
          }
        }
      }
      if(isDead)
        _revision.setText("(" + rev.getRevisionNumber().asString() + ")");
      else
        _revision.setText(rev.getRevisionNumber().asString());
      if(rev.getRevisionNumber().asString().equals(_currentRevision))
        _panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(JBColor.black,4),BorderFactory.createEmptyBorder(CELL_MARGIN,CELL_MARGIN,CELL_MARGIN,CELL_MARGIN)));
      else
        _panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(JBColor.black,1),BorderFactory.createEmptyBorder(CELL_MARGIN,CELL_MARGIN,CELL_MARGIN,CELL_MARGIN)));
      _author.setText(rev.getAuthor());
      if(selected) {
        if(isDead) {
          _panel.setBackground(_revisionDeletedSelectedColor);
          _ta.setBackground(_revisionDeletedSelectedColor);
        } else {
          _panel.setBackground(_revisionSelectedColor);
          _ta.setBackground(_revisionSelectedColor);
        }
        _revision.setForeground(JBColor.white);
        _author.setForeground(JBColor.white);
        _sep.setForeground(JBColor.white);
        _ta.setForeground(JBColor.white);
      } else {
        if(isDead) {
          _panel.setBackground(_revisionDeletedColor);
          _ta.setBackground(_revisionDeletedColor);
        } else {
          _panel.setBackground(_revisionColor);
          _ta.setBackground(_revisionColor);
        }
        _revision.setForeground(JBColor.black);
        _author.setForeground(JBColor.black);
        _sep.setForeground(JBColor.black);
        _ta.setForeground(JBColor.black);
      }
      //Add separator
      if((revLabels != null) && (revLabels.size() > 0)) {
        _sep.setVisible(true);
        _ta.setVisible(true);
        _tl.setRow(1,TableLayout.PREFERRED);
        _tl.setRow(2,TableLayout.PREFERRED);
        StringBuilder labels = new StringBuilder();
        for(String revLabel : revLabels) {
          if(labels.length() != 0)
            labels.append("\n");
          labels.append(revLabel);
        }
        _ta.setText(labels.toString());
      } else {
        _sep.setVisible(false);
        _ta.setVisible(false);
        _tl.setRow(1,0.0);
        _tl.setRow(2,0.0);
        _ta.setText(null);
      }
      return(_panel);
    }
  }
}
