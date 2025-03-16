package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

interface TransactionFunction2Param<R, P1, P2> {
	R apply(P1 param1, P2 param2) throws SQLException;
}

interface TransactionFunction<R, P> {
	R apply(P param) throws SQLException;
}

public abstract class BaseService {
	private final Connection connection;

	public BaseService(Connection connection) {
		this.connection = connection;
	}

	public <R, P1, P2> Optional<R> runTransaction(TransactionFunction2Param<R, P1, P2> function, P1 param1, P2 param2)
			throws SQLException {
		try {
			this.connection.setAutoCommit(false);
			R result = function.apply(param1, param2);
			this.connection.commit();
			this.connection.setAutoCommit(true);
			return Optional.of(result);
		} catch (SQLException e) {
			this.connection.rollback();
			this.connection.setAutoCommit(true);
			throw e;
		}
	}

	public <R, P> Optional<R> runTransaction(TransactionFunction<R, P> function, P param) throws SQLException {
		try {
			this.connection.setAutoCommit(false);
			R result = function.apply(param);
			this.connection.commit();
			this.connection.setAutoCommit(true);
			return Optional.of(result);
		} catch (SQLException e) {
			this.connection.rollback();
			this.connection.setAutoCommit(true);
			throw e;
		}
	}
}
