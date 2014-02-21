package com.everypay.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.everypay.api.Server;
import com.everypay.exceptions.ServerConnectionException;
import com.everypay.model.Card;
import com.everypay.model.Company;
import com.everypay.model.Customer;
import com.everypay.model.Payment;
import com.everypay.model.Token;
import com.everypay.model.Token.TokenType;

public class ApiTest {
	
	private String companyToken = "sk_chgRcz8C2UvhytYlzEcS86KpAsHenMOG";
	private String cardToken = "crd_f6048bf28e70259fa9eb5419a4b8453f";
	private String username = "mkitsos";
	private String password = "1111";
	private Card card;
	private Company company;
	private Customer customer;
	private Payment payment;
	private List<Customer> listCustomers;
	private List<Payment> listPayments;
	

	@Before
	public void setUp() throws Exception {
		company = new Company();
		company.setSessionToken(new Token(companyToken, TokenType.Company));
		card = new Card();
		card.setNumber("4140281556139011");
		card.setExpiration(new Date(2014, 12, 0));
		card.setCvv("323");
//		card.setExpirationYear(2014);
//		card.setExpirationMonth(12);
//		card.setLastFour("9011");
//		card.setHolderName("Αβφκξδδφκ");
//		card.setToken(new Token(cardToken, TokenType.Card));
		
		payment = new Payment();
		payment.setAmount(1100);
		payment.setCurrency("EUR");
		payment.setDescription("ΑΒΓΔ αβγδ");
	}

	@Test
	public void testAuthorization() {

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					company = Server.get().authorize(username, password, null);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});
		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// assertTrue(card.getToken().getValue().length()>0);
		if (company.getSessionToken() != null && company.getKey()!=null)
			assertTrue((company.getSessionToken().getValue().length() > 0) && (company.getKey().length() > 0));
		else
			fail();

	}
	

	@Test
	public void testIsAuthorized() {

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					company = Server.get().authorize(username, password, null);
					boolean res = Server.get().isAuthorized(company);
						assertTrue(res);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});
		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// assertTrue(card.getToken().getValue().length()>0);

	}
	
	@Test
	public void testLogout() {

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					company = Server.get().authorize(username, password, null);
					Server.get().logout(company, null);
					boolean res = Server.get().isAuthorized(company);
						assertTrue(!res);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});
		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// assertTrue(card.getToken().getValue().length()>0);

	}

	
	@Test
	public void testCreateCardToken() {

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get().createCardToken(
							company, card);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});
		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// assertTrue(card.getToken().getValue().length()>0);
		if (card.getToken().getValue() != null)
			assertTrue(card.getToken().getValue().length() > 0);
		else
			fail();

	}

	@Test
	public void testFindCardToken() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get().createCardToken(
							company, card);
					Server.get().findCardToken(
							company, card);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(card.getLastFour().length() > 0);

	}

	@Test
	public void testListCustomers() {
		listCustomers = new ArrayList<Customer>();
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					listCustomers = Server.get()
							.listCustomers(company);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(listCustomers.size() > 0);

	}

	@Test
	public void testCreateCustomerWithCard() {
		customer = new Customer();
		customer.setName("Test Name 1");
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get()
							.createCustomerWithCard(company, card, customer);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(customer.getToken() != null);

	}

	@Test
	public void testCreateCustomerWithCardToken() {
		customer = new Customer();
		customer.setName("Test Name 1");
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get().createCardToken(
							company, card);
					Server.get()
							.createCustomerWithCardToken(company, card,
									customer);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(customer.getToken() != null);

	}

	@Test
	public void testFindCustomer() {
		customer = new Customer();
		customer.setName("Test Name 1");
		final Customer customer1 = new Customer();
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get().createCardToken(
							company, card);
					Server.get()
							.createCustomerWithCardToken(company, card,
									customer);
					customer1.setToken(customer.getToken());
					Server.get().findCustomer(
							company, customer1);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(customer1.getName().length() > 0);

	}

	@Test
	public void testUpdateCustomer() {
		customer = new Customer();
		// customer.setToken(new
		// Token("cus_OrjjmFPPUatxyRTNvQ9szsAm",TokenType.Customer));
		// customer.setEmail("test@test.com");
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get()
							.createCustomerWithCard(company, card, customer);
					customer.setEmail("test@test.com");
					customer.setName("Test Name");
					Server.get().updateCustomer(
							company, customer, null);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			Server.get().findCustomer(company, customer);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}
		assertTrue(customer.getEmail().equalsIgnoreCase("test@test.com"));
		assertTrue(customer.getName().equalsIgnoreCase("Test Name"));

	}

	// Use case ??????????????????????????????????????
	// public void testUpdateCustomerCardToken(){
	// customer = new Customer();
	// ExecutorService service = Executors.newSingleThreadExecutor();
	// service.execute(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// com.everypay.serverlib.Server.get(null).createCustomerWithCard(company,
	// card, customer);
	// com.everypay.serverlib.Server.get(null).updateCustomerCardToken(company,
	// customer, new Token("",TokenType.Card));
	// } catch (ServerConnectionException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	//
	// try {
	// service.awaitTermination(5, TimeUnit.SECONDS);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	//
	// }

	@Test
	public void testDeleteCustomer() {
		customer = new Customer();
		customer.setEmail("test@test.com");
		customer.setName("Test Name 1");
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get()
							.createCustomerWithCard(company, card, customer);
					assertTrue(Server.get()
							.findCustomer(company, customer));
					Server.get().deleteCustomer(
							company, customer);
					assertTrue(!Server.get()
							.findCustomer(company, customer));
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testListPayments() {
		listPayments = new ArrayList<Payment>();
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					listPayments = Server.get()
							.listPayments(company);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(listPayments.size() > 0);

	}

	@Test
	public void testFindPayment() {
		listPayments = new ArrayList<Payment>();
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					listPayments = Server.get()
							.listPayments(company);
					Payment payment = listPayments.get(0);
					assertTrue(Server.get()
							.findPayment(company, payment));
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testPaymentWithCard() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					company = Server.get().authorize("mkitsos", "1111", null);
					Server.get().newPaymentWithCard(
							company, card, payment);
					assertTrue(Server.get()
							.findPayment(company, payment));
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSendReceipt() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					company = Server.get().authorize("mkitsos", "1111", null);
					Server.get().newPaymentWithCard(
							company, card, payment);
					Server.get().sendReceipt(company, payment, "alex.halevin@gmail.com", null);
					assertTrue(true);
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	
	
	@Test
	public void testPaymentWithCardToken() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get().createCardToken(
							company, card);
					Server.get()
							.newPaymentWithCardToken(company, card.getToken().getValue(), payment);
					assertTrue(Server.get()
							.findPayment(company, payment));
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testPaymentWithCustomerToken() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					listCustomers = Server.get()
							.listCustomers(company);
					if (listCustomers != null) {
						if (listCustomers.size() > 0)
							Server.get()
									.newPaymentWithCustomerToken(company,
											listCustomers.get(0), payment);
						assertTrue(Server.get()
								.findPayment(company, payment));
					} else {
						fail();
					}
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testRefundPayment() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Server.get().newPaymentWithCard(
							company, card, payment);
					Server.get().refundPayment(
							company, payment);
					Server.get().findPayment(company, payment);
					assertTrue(payment.isRefunded());
				} catch (ServerConnectionException e) {
					e.printStackTrace();
					fail();
				}
			}
		});

		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
}

