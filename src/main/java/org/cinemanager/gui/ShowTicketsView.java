package org.cinemanager.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.cinemanager.common.ShowingVersion;
import org.cinemanager.common.TicketType;
import org.cinemanager.controller.TicketController;
import org.cinemanager.entity.IEntity;
import org.cinemanager.entity.Seat;
import org.cinemanager.entity.Ticket;
import org.cinemanager.gui.EntityList.DeleteActionListenerCreator;
import org.cinemanager.gui.EntityList.EntityFormatter; 


public class ShowTicketsView extends View<Ticket> {
	
	private static final long serialVersionUID = 1L;
	private static final String APPLY_BUTTON_LABEL = "Done";
	private static final String CANCEL_BUTTON_LABEL = "Back";
	
	private static JList<Ticket> ticketList;
	private JScrollPane scroll;
	
	private static final TicketController controller = TicketController.getInstance();    
	
	public ShowTicketsView(ViewManager viewManager) { 
		setLayout(new BorderLayout());
		
		List<Ticket> tickets = controller.getAllTickets();
		ticketList = new EntityList<Ticket>(tickets, new TicketFormatter(), new ActionListenerCreator());    
		 
		scroll = new JScrollPane(ticketList);
		this.add(scroll);
	} 
	@Override
	public boolean hasAnyChanges() {
		return false;
	}

	@Override
	public boolean areInputsValid() {
		return true;
	}

	@Override
	public void doApplyAction() {

	}

	@Override
	public Ticket doGetResultAction() {
		return  ticketList.getSelectedValue();
	}
	
	@Override
	public void handleRequestedResult(IEntity result) {
		
	}

	@Override
	public String getApplyButtonLabel() {
		return APPLY_BUTTON_LABEL;
	}
	
	@Override
	public String getCancelButtonLabel() {
		return CANCEL_BUTTON_LABEL;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
	public static ViewCreator<ShowTicketsView> getCreator() {
		return new ShowTicketsViewCreator();
	}
	
	private static class ShowTicketsViewCreator implements ViewCreator<ShowTicketsView> {
		@Override
		public ShowTicketsView createView(ViewManager viewManager) {
			return new ShowTicketsView(viewManager);
		}
		
	} 
	
	private static class TicketFormatter implements EntityFormatter<Ticket> {
		private static final String DATE_FORMAT = "HH:mm 'on' dd MMM yyyy";  
		private static final SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH); 
		
		@Override
		public String getLabelText(Ticket entity) {
			return parseType(entity.getType()) + 
					" for " + entity.getShowing().getMovie().getTitle() + parseVersion(entity.getShowing().getVersion()) + 
					" at " + parseDate(entity.getShowing().getDate()) + 
					" seat " + parseSeat(entity.getSeat()) +
					" costing " + parsePrice(entity.getPrice());
		} 
		
		private String parseSeat(Seat seat) {
			return convertNumberToLetter(seat.getRow()) + seat.getNumber() + (seat.isVip() ? " vip" : "");
		}
		
		private String parsePrice(int price) {
			return String.valueOf((int)price/100) + "." + String.valueOf(price % 100 - price % 10) + String.valueOf(price % 10);
		}
		
		private String parseType(TicketType type) {
			return type.toString().toLowerCase();
		}
		
		private String parseDate(Date date) {
			return dateParser.format(date);
		}
		
		private String parseVersion(ShowingVersion version) {
			if(version == ShowingVersion.VERSION_2D) {
				return " 2D";
			} else {
				return " 3D";
			}
		}
		
		private static String convertNumberToLetter(int i) {
			return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(i, i+1);
		}
	}
	private static class ActionListenerCreator implements DeleteActionListenerCreator {

		@Override
		public ActionListener create(Long id, ActionListener deleteSuccessfulCallback) {
			return new DeleteActionListener(id, deleteSuccessfulCallback);
		}
	}
	
	private static class DeleteActionListener implements ActionListener {
		
		private final Long id;
		private final ActionListener callback;
		
		public DeleteActionListener(Long id, ActionListener callback) {
			this.id = id;
			this.callback = callback;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ( isUserSureToDeleteTicket() ) { 
				controller.deleteTicket(id);
				callback.actionPerformed(null); 
			} 
		}
		
		private boolean isUserSureToDeleteTicket() {
			return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(ticketList, "Are you sure you want to delete this entry?", null, JOptionPane.YES_NO_OPTION);
		}
	}
}
