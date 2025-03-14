import { Client } from '@stomp/stompjs';
import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import Card from './Card'
import { StockAnalysis } from '../types';

export default function PopularStocksGrid() {
	const [popularStocks, setPopularStocks] = useState<StockAnalysis[]>([]);

	// WebSocket connection to server
	useEffect(() => {
		const socket = new SockJS('http://localhost:8080/ws');
		const stompClient = new Client({ 
			webSocketFactory: () => socket, 
			reconnectDelay: 5000,
			onConnect: () => {
				console.log("Connected to WebSocket");

				// TODO: Initial fetch all data from database


				stompClient.subscribe('/topic/popular', (message) => {
					const stockAnalysis: StockAnalysis = JSON.parse(message.body);
					console.log(stockAnalysis);

					// Add to list. If stock already exists, update element
					setPopularStocks((prev) => {
						const index = prev.findIndex((item) => item.stock.symbol === stockAnalysis.stock.symbol);
						if (index === -1) {
							return [...prev, stockAnalysis];
						} else {
							prev[index] = stockAnalysis;
							return prev;
						}
					});
				});
			},
			onStompError: (frame) => {
				console.error('Broker reported error: ' + frame.headers['message']);
				console.error('Additional details: ' + frame.body);
			}
		});

		stompClient.activate();

		return () => {
			stompClient.deactivate();
		};
	}, []);

  return (
		<div className='h-screen'>
			<div className="grid grid-cols-4 gap-8 mt-4">
				{/* {popularStocks.map((stock) => (
					<Card key={stock.symbol} stock={stock} imageUrl={"https://cdn.brandfetch.io/idnrCPuv87/w/400/h/400/theme/dark/icon.png?c=1dxbfHSJFAPEGdCLU4o5B"} />
				))} */}
				{/* <Card stock={test} imageUrl="https://g.foolcdn.com/art/companylogos/square/nvda.png" />
				<Card stock={test} imageUrl="https://www.apple.com/ac/structured-data/images/knowledge_graph_logo.png?202305280847" />
				<Card stock={test} imageUrl="https://pngimg.com/d/microsoft_PNG13.png" />
				<Card stock={test} imageUrl="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPQEF2XXwt44laODjvpEpvgUlajKbkdmBeEw&s" />
				<Card stock={test} imageUrl="https://avatars.githubusercontent.com/u/8594673?s=200&v=4" />
				<Card stock={test} imageUrl="https://upload.wikimedia.org/wikipedia/commons/e/e8/Tesla_logo.png" />
				<Card stock={test} imageUrl="https://cdn1.iconfinder.com/data/icons/google-s-logo/150/Google_Icons-09-512.png" /> */}
			</div>
		</div>
  )
}
