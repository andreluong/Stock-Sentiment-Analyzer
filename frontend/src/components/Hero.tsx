import { Chip } from '@heroui/chip'

export default function Hero() {
  return (
    <div className="gradient-background text-white py-16 my-auto">
			<h1 className="text-5xl font-bold py-4">Stock Sentiment Analyzer</h1>
			<p className="text-md">Analyze stocks using sentiment analysis on Yahoo Finance news articles</p>

			<div className="flex flex-row justify-center py-6 gap-4">
				<Chip variant="flat" className="bg-white hover-shadow">
					<div className="flex flex-row">
						<p>📰&nbsp;</p>
						<p className="gradient-text">Live News Data</p>
					</div>
				</Chip>
				<Chip variant="flat" className="bg-white hover-shadow">
					<div className="flex flex-row">
						<p>📈&nbsp;</p>
						<p className="gradient-text">Sentiment Scores</p>
					</div>
				</Chip>
				<Chip variant="flat" className="bg-white hover-shadow">
					<div className="flex flex-row">
						<p>💡&nbsp;</p>
						<p className="gradient-text">Data-Driven Insights</p>
					</div>
				</Chip>
			</div>
		</div>
  )
}
