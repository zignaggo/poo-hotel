package poo.domain.services;

import java.sql.Connection;
import java.util.Optional;

interface TransactionFunction2Param<R, P1, P2> {
	R apply(P1 param1, P2 param2) throws Exception;
}

interface TransactionFunction3Param<R, P1, P2, P3> {
	R apply(P1 param1, P2 param2, P3 param3) throws Exception;
}

interface TransactionFunction<R, P> {
	R apply(P param) throws Exception;
}

public abstract class BaseService {
	private final Connection connection;

	public BaseService(Connection connection) {
		this.connection = connection;
	}

	public <R, P1, P2> Optional<R> runTransaction(TransactionFunction2Param<R, P1, P2> function, P1 param1, P2 param2)
			throws Exception {
		try {
			this.connection.setAutoCommit(false);
			R result = function.apply(param1, param2);
			this.connection.commit();
			this.connection.setAutoCommit(true);
			return Optional.of(result);
		} catch (Exception e) {
			this.connection.rollback();
			this.connection.setAutoCommit(true);
			throw e;
		}
	}

	public <R, P> Optional<R> runTransaction(TransactionFunction<R, P> function, P param) throws Exception {
		try {
			this.connection.setAutoCommit(false);
			R result = function.apply(param);
			this.connection.commit();
			this.connection.setAutoCommit(true);
			return Optional.of(result);
		} catch (Exception e) {
			this.connection.rollback();
			this.connection.setAutoCommit(true);
			throw e;
		}
	}

	public <R, P1, P2, P3> Optional<R> runTransaction(TransactionFunction3Param<R, P1, P2, P3> function, P1 param1,
			P2 param2, P3 param3) throws Exception {
		try {
			this.connection.setAutoCommit(false);
			R result = function.apply(param1, param2, param3);
			this.connection.commit();
			this.connection.setAutoCommit(true);
			return Optional.of(result);
		} catch (Exception e) {
			this.connection.rollback();
			this.connection.setAutoCommit(true);
			throw e;
		}
	}
}
