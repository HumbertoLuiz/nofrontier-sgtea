package br.com.xfrontier.sgetea.core.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String msg) {
		super(msg);
	}
}
