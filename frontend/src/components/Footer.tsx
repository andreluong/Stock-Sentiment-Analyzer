import { Icon } from "@iconify/react/dist/iconify.js";

export default function Footer() {
	return (
		<footer className="gradient-background text-white text-center mt-8 pt-4 pb-48">
			<p>Stock Sentiment Analyzer | 2025</p>
			<a href='https://github.com/andreluong/Stock-Sentiment-Analyzer'>
                <Icon icon='mdi:github' className='text-4xl' />
            </a>
		</footer>
	)
}
