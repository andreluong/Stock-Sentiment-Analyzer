import { CircularProgress } from '@heroui/react';
import { StockAnalysis } from '../types';
import Card from './Card';

export default function Searches({
	isLoading
}: {
	isLoading: boolean
}) {
	const stockAnalysis: StockAnalysis[] = [];
	const keys = Object.keys(sessionStorage);
	for (let i = 0; i < keys.length; i++) {
		if (!keys[i].startsWith("stock:")) continue;

		const item = sessionStorage.getItem(keys[i])
		if (item) {
			stockAnalysis.push(JSON.parse(item));
		}
	}

  return (
		<div className="h-screen">
			{isLoading && (
				<CircularProgress label="Loading..." color="success" size="lg" className='mx-auto' />
			)}
			{!isLoading && (
				<div>
					{stockAnalysis.length == 0 && <p className="m-20">No searches yet</p>}
					{stockAnalysis.length > 0 && (
						<div className="grid grid-cols-4 gap-8 mt-4">
							{stockAnalysis.map((s) => (
								<Card
									key={s.stock.symbol}
									stockAnalysis={s}
									imageUrl={`https://cdn.brandfetch.io/idnrCPuv87/w/400/h/400/theme/dark/icon.png?c=1dxbfHSJFAPEGdCLU4o5B`}
								/>
							))}
						</div>
					)}
				</div>
			)}
		</div>
  )
}
