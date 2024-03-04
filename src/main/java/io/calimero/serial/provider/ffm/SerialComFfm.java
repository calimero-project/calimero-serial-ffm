/*
    Calimero 2 - A library for KNX network access
    Copyright (c) 2024, 2024 B. Malinowsky

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Linking this library statically or dynamically with other modules is
    making a combined work based on this library. Thus, the terms and
    conditions of the GNU General Public License cover the whole
    combination.

    As a special exception, the copyright holders of this library give you
    permission to link this library with independent modules to produce an
    executable, regardless of the license terms of these independent
    modules, and to copy and distribute the resulting executable under terms
    of your choice, provided that you also meet, for each linked independent
    module, the terms and conditions of the license of that module. An
    independent module is a module which is not derived from or based on
    this library. If you modify this library, you may extend this exception
    to your version of the library, but you are not obligated to do so. If
    you do not wish to do so, delete this exception statement from your
    version.
*/

package io.calimero.serial.provider.ffm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.calimero.KNXException;
import io.calimero.serial.spi.SerialCom;
import io.calimero.serial.spi.SerialConnectionProvider;
import serial.ffm.SerialPort;

final class SerialComFfm implements SerialCom {
	private final SerialPort port;

	SerialComFfm(final SerialConnectionProvider.Settings settings) throws KNXException {
		try {
			port = SerialPort.open(settings.portId())
					.baudRate(settings.baudrate())
					.dataBits(settings.databits())
					.stopBits(stopBits(settings.stopbits()))
					.parity(parity(settings.parity()))
					.flowControl(flowControl(settings.flowControl()))
					.timeouts(timeouts(settings));
		} catch (final IOException e) {
			throw new KNXException("opening " + settings, e);
		}
	}

	@Override
	public int baudRate() throws IOException { return port.baudRate(); }

	@Override
	public InputStream inputStream() { return port.inputStream(); }

	@Override
	public OutputStream outputStream() { return port.outputStream(); }

	@Override
	public void close() { port.close(); }

	private static SerialPort.StopBits stopBits(final StopBits stopbits) {
		return switch (stopbits) {
			case One -> SerialPort.StopBits.One;
			case Two -> SerialPort.StopBits.Two;
		};
	}

	private static SerialPort.Parity parity(final Parity parity) {
		return switch (parity) {
			case None -> SerialPort.Parity.None;
			case Odd -> SerialPort.Parity.Odd;
			case Even -> SerialPort.Parity.Even;
			case Mark -> SerialPort.Parity.Mark;
		};
	}

	private static SerialPort.FlowControl flowControl(final FlowControl flowControl) {
		return switch (flowControl) {
			case None -> SerialPort.FlowControl.None;
		};
	}

	private static SerialPort.Timeouts timeouts(final SerialConnectionProvider.Settings settings) {
		return new SerialPort.Timeouts((int) settings.readIntervalTimeout().toMillis(),
				0, (int) settings.receiveTimeout().toMillis(), 0, 0);
	}
}
